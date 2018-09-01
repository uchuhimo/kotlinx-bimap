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

@file:kotlin.jvm.JvmName("BiMapsKt")
@file:kotlin.jvm.JvmMultifileClass

package com.uchuhimo.collections

import com.google.common.collect.HashBiMap
import com.google.common.collect.BiMap as GuavaBiMap

/**
 * Views a mutable bimap as a Guava bimap. The original bimap shares same contents
 * with the returned Guava bimap, so changes to the original bimap are reflected
 * in the returned Guava bimap, and vice-versa.
 *
 * @receiver a mutable bimap
 * @return a Guava bimap
 */
fun <K, V> MutableBiMap<K, V>.asGuavaBiMap(): GuavaBiMap<K, V> =
         (this as? MutableBiMapWrapper)?.delegate ?: GuavaBiMapWrapper(this)

/**
 * Views a Guava bimap as a mutable bimap. The original Guava bimap shares same
 * contents with the returned bimap, so changes to the original Guava bimap are
 * reflected in the returned bimap, and vice-versa.
 *
 * @receiver a Guava bimap
 * @return a mutable bimap
 */
fun <K, V> GuavaBiMap<K, V>.asMutableBiMap(): MutableBiMap<K, V> =
        (this as? GuavaBiMapWrapper)?.delegate ?: MutableBiMapWrapper(this)

/**
 * Returns a new mutable bimap with the specified contents, given as a list of pairs
 * where the first value is the key and the second is the value.
 *
 * If multiple pairs have the same key or the same value, the resulting bimap will contain
 * the last of those pairs.
 *
 * Entries of the bimap are iterated in the order they were specified.
 *
 * @param pairs the specified contents for the returned bimap
 * @return a new mutable bimap
 */
fun <K, V> mutableBiMapOf(vararg pairs: Pair<K, V>): MutableBiMap<K, V> = HashBiMap.create<K, V>(pairs.size).asMutableBiMap().apply { putAll(pairs) }

/**
 * Returns a new mutable bimap containing all key-value pairs from the given map.
 *
 * The returned bimap preserves the entry iteration order of the original map.
 *
 * @receiver the original map
 * @return a new mutable bimap
 */
fun <K, V> Map<K, V>.toMutableBiMap(): MutableBiMap<K, V> = HashBiMap.create<K, V>(size).asMutableBiMap().apply { putAll(this@toMutableBiMap) }

internal class MutableBiMapWrapper<K, V>(internal val delegate: GuavaBiMap<K, V>) :
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

internal class GuavaBiMapWrapper<K, V>(internal val delegate: MutableBiMap<K, V>) :
        GuavaBiMap<K, V> {
    override fun putAll(from: Map<out K, V>) = delegate.putAll(from)

    override val size: Int get() = delegate.size

    override fun containsKey(key: K): Boolean = delegate.containsKey(key)

    override fun get(key: K): V? = delegate[key]

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
