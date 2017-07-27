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

/**
 * A bimap (or "bidirectional map") is a map that preserves the uniqueness of
 * its values as well as that of its keys. This constraint enables bimaps to
 * support an "inverse view", which is another bimap containing the same entries
 * as this bimap but with reversed keys and values.
 *
 * @param K the type of map keys.
 * @param V the type of map values.
 * @see MutableBiMap
 */
interface BiMap<K, V> : Map<K, V> {
    /**
     * Returns the inverse view of this bimap, which maps each of this bimap's
     * values to its associated key. The two bimaps are backed by the same data;
     * any changes to one will appear in the other.
     *
     * __Note:__ There is no guaranteed correspondence between the iteration
     * order of a bimap and that of its inverse.
     *
     * @return the inverse view of this bimap
     */
    val inverse: BiMap<V, K>

    /**
     * Returns a [Set] view of the values contained in this bimap.
     *
     * The set is backed by the bimap, so changes to the bimap are
     * reflected in the set.
     *
     * @return a set view of the values contained in this bimap
     */
    override val values: Set<V>
}
