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
