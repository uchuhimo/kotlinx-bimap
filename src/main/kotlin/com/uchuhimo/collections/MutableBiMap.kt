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

private class MutableBiMapImpl<K, V> private constructor(private val delegate: MutableMap<K, V>) :
        MutableBiMap<K, V>, Map<K, V> by delegate {
    constructor(forward: MutableMap<K, V>, backward: MutableMap<V, K>) : this(forward) {
        _inverse = MutableBiMapImpl(backward, this)
    }

    private constructor(backward: MutableMap<K, V>, forward: MutableBiMapImpl<V, K>) : this(backward) {
        _inverse = forward
    }

    private lateinit var _inverse: MutableBiMapImpl<V, K>

    private val inverseDelegate = inverse.delegate

    override val inverse: MutableBiMapImpl<V, K> get() = _inverse

    override fun containsValue(value: V): Boolean = inverseDelegate.containsKey(value)

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> =
            object : MutableSet<MutableMap.MutableEntry<K, V>> by delegate.entries {
                override fun clear() {
                    this@MutableBiMapImpl.clear()
                }

                override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> =
                        object : MutableIterator<MutableMap.MutableEntry<K, V>> {
                            override fun remove() {
                                TODO("not implemented")
                            }

                            override fun hasNext(): Boolean {
                                TODO("not implemented")
                            }

                            override fun next(): MutableMap.MutableEntry<K, V> {
                                TODO("not implemented")
                            }
                        }

                override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
                    inverseDelegate.remove(element.value, element.key)
                    return delegate.remove(element.key, element.value)
                }
            }

    override val keys: MutableSet<K>
        get() = TODO("not implemented")
    override val values: MutableSet<V>
        get() = TODO("not implemented")

    override fun clear() {
        delegate.clear()
        inverseDelegate.clear()
    }

    override fun put(key: K, value: V): V? {
        inverseDelegate.put(value, key)?.also { oldKey -> delegate.remove(oldKey) }
        return delegate.put(key, value)?.also { oldValue -> inverseDelegate.remove(oldValue) }
    }

    override fun forcePut(key: K, value: V): V? {
        TODO("not implemented")
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { key, value -> this[key] = value }
    }

    override fun remove(key: K): V? {
        return delegate.remove(key).also { value ->
            if (value != null) {
                inverseDelegate.remove(value)
            }
        }
    }
}

typealias GuavaBiMap<K, V> = com.google.common.collect.BiMap<K, V>

class MutableBiMapWrapper<K, V>(internal val delegate: GuavaBiMap<K, V>) :
        MutableBiMap<K, V>, MutableMap<K, V> by delegate {
    override val inverse: MutableBiMap<V, K> = InverseWrapper(this)

    override val values: MutableSet<V> = delegate.values

    override fun forcePut(key: K, value: V): V? = delegate.forcePut(key, value)

    companion object {
        private class InverseWrapper<K, V>(private val wrapper: MutableBiMapWrapper<V, K>) :
                MutableBiMap<K, V>, MutableMap<K, V> by wrapper.delegate.inverse() {
            override val inverse: MutableBiMap<V, K> = wrapper

            override val values: MutableSet<V> = wrapper.delegate.inverse().values

            override fun forcePut(key: K, value: V): V? =
                    wrapper.delegate.inverse().forcePut(key, value)
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

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> mutableBiMapOf(): MutableBiMap<K, V> = HashBiMap.create<K, V>().asMutableBiMap()

fun <K, V> mutableBiMapOf(vararg pairs: Pair<K, V>): MutableBiMap<K, V>
        = HashBiMap.create<K, V>(pairs.size).asMutableBiMap().apply { putAll(pairs) }
