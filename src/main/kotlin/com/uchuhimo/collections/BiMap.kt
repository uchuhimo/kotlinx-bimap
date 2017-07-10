package com.uchuhimo.collections

interface BiMap<K, V> : Map<K, V> {
    val inverse: BiMap<V, K>

    override val values: Set<V>
}

private class BiMapImpl<K, V> private constructor(delegate: Map<K, V>) :
        BiMap<K, V>, Map<K, V> by delegate {
    constructor(forward: Map<K, V>, backward: Map<V, K>) : this(forward) {
        _inverse = BiMapImpl(backward, this)
    }

    private constructor(backward: Map<K, V>, forward: BiMap<V, K>) : this(backward) {
        _inverse = forward
    }

    private lateinit var _inverse: BiMap<V, K>

    override val inverse: BiMap<V, K> get() = _inverse

    override val values: Set<V> get() = inverse.keys
}

private val emptyBiMap = BiMapImpl<Any?, Any?>(emptyMap(), emptyMap())

fun <K, V> emptyBiMap(): BiMap<K, V> = @Suppress("UNCHECKED_CAST") (emptyBiMap as BiMap<K, V>)

fun <K, V> biMapOf(vararg pairs: Pair<K, V>): BiMap<K, V> =
        if (pairs.isNotEmpty()) {
            val inversePairs = Array(pairs.size, { i -> pairs[i].second to pairs[i].first })
            BiMapImpl(mapOf(*pairs), mapOf(*inversePairs))
        } else {
            emptyBiMap()
        }

@Suppress("NOTHING_TO_INLINE")
inline fun <K, V> biMapOf(): Map<K, V> = emptyBiMap()

fun <K, V> biMapOf(pair: Pair<K, V>): Map<K, V> =
        BiMapImpl(mapOf(pair), mapOf(pair.second to pair.first))
