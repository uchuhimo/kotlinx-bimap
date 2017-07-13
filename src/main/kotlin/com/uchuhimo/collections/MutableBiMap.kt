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
