/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uchuhimo.collections

import com.google.common.collect.HashBiMap

interface MutableBiMap<K, V> : MutableMap<K, V>, BiMap<K, V> {
    override val inverse: MutableBiMap<V, K>

    override val values: MutableSet<V>

    /**
     * Associates the specified [value] with the specified [key] in the bimap.
     *
     * The bimap throws [IllegalArgumentException] if the given value is already
     * bound to a different key in it. The bimap will remain unmodified in this
     * event. To avoid this exception, call [forcePut] instead.
     *
     * @return the previous value associated with the key, or `null` if the key
     *         was not present in the bimap.
     */
    override fun put(key: K, value: V): V?

    /**
     * An alternate form of [put] that silently removes any existing entry
     * with the value [value] before proceeding with the [put] operation.
     *
     * If the bimap previously contained the provided key-value
     * mapping, this method has no effect.
     *
     * Note that a successful call to this method could cause the size of the
     * bimap to increase by one, stay the same, or even decrease by one.
     *
     * **Warning**: If an existing entry with this value is removed, the key
     * for that entry is discarded and not returned.
     *
     * @param key the key with which the specified value is to be associated.
     * @param value the value to be associated with the specified key.
     * @return the value which was previously associated with the key, or `null`
     *         if there was no previous entry.
     */
    fun forcePut(key: K, value: V): V?
}

typealias GuavaBiMap<K, V> = com.google.common.collect.BiMap<K, V>

class MutableBiMapWrapper<K, V>(internal val delegate: GuavaBiMap<K, V>) :
        MutableBiMap<K, V>, MutableMap<K, V> by delegate {
    override val inverse: MutableBiMap<V, K> = InverseWrapper(this)

    override val values: MutableSet<V> = delegate.values

    override fun forcePut(key: K, value: V): V? = delegate.forcePut(key, value)

    override fun equals(other: Any?): Boolean = equals(this, other)

    override fun hashCode(): Int = hashCodeOf(this)

    companion object {
        private class InverseWrapper<K, V>(private val wrapper: MutableBiMapWrapper<V, K>) :
                MutableBiMap<K, V>, MutableMap<K, V> by wrapper.delegate.inverse() {
            override val inverse: MutableBiMap<V, K> = wrapper

            override val values: MutableSet<V> = wrapper.delegate.inverse().values

            override fun forcePut(key: K, value: V): V? =
                    wrapper.delegate.inverse().forcePut(key, value)

            override fun equals(other: Any?): Boolean = equals(this, other)

            override fun hashCode(): Int = hashCodeOf(this)
        }
    }
}

class GuavaBiMapWrapper<K, V>(internal val delegate: MutableBiMap<K, V>) :
        GuavaBiMap<K, V> {
    override fun putAll(from: Map<out K, V>) = delegate.putAll(from)

    override val size: Int get() = delegate.size

    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun get(key: K): V? = delegate.get(key)

    override fun remove(key: K): V? = delegate.remove(key)

    override val values: MutableSet<V> get() = delegate.values

    override fun containsValue(value: V): Boolean = delegate.containsValue(value)

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun forcePut(key: K?, value: V?): V? = delegate.forcePut(key!!, value!!)

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = delegate.entries

    override val keys: MutableSet<K> get() = delegate.keys

    override fun put(key: K?, value: V?): V? = delegate.put(key!!, value!!)

    private val _inverse: GuavaBiMap<V, K> = delegate.inverse.asGuavaBiMap()

    override fun inverse(): GuavaBiMap<V, K> = _inverse

    override fun clear() = delegate.clear()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GuavaBiMap<*, *>) return false
        if (other.size != size) return false
        val i = entries.iterator()
        while (i.hasNext()) {
            val e = i.next()
            val key = e.key
            val value = e.value
            if (value == null) {
                if (other[key] != null || !other.containsKey(key))
                    return false
            } else {
                if (value != other[key])
                    return false
            }
        }
        return true
    }

    override fun hashCode(): Int = hashCodeOf(this)
}

fun <K, V> MutableBiMap<K, V>.asGuavaBiMap(): GuavaBiMap<K, V> =
        if (this is MutableBiMapWrapper) {
            delegate
        } else {
            GuavaBiMapWrapper(this)
        }

fun <K, V> GuavaBiMap<K, V>.asMutableBiMap(): MutableBiMap<K, V> =
        if (this is GuavaBiMapWrapper) {
            delegate
        } else {
            MutableBiMapWrapper(this)
        }

fun <K, V> mutableBiMapOf(vararg pairs: Pair<K, V>): MutableBiMap<K, V>
        = HashBiMap.create<K, V>(pairs.size).asMutableBiMap().apply { putAll(pairs) }
