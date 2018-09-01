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

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.Spek
import kotlin.test.assertNull

object MutableBiMapCoverageSpec : Spek({
    test("tests for coverage") {
        val mutableBiMap = mutableBiMapOf<Int, String>()
        val guavaBiMap = GuavaBiMapWrapper(mutableBiMap)
        assertThat(GuavaBiMapWrapper(mutableBiMap).asMutableBiMap(), equalTo(mutableBiMap))
        assertThat(guavaBiMap, equalTo(guavaBiMap))
        assertThat(guavaBiMap, !equalTo(mapOf<Int, String>()))
        assertThat(GuavaBiMapWrapper(mutableBiMapOf<Int, String?>(1 to null)),
            !equalTo(GuavaBiMapWrapper(mutableBiMapOf<Int, String?>(1 to "string"))))
        assertThat(GuavaBiMapWrapper(mutableBiMapOf<Int, String?>(1 to null)),
            !equalTo(GuavaBiMapWrapper(mutableBiMapOf<Int, String?>())))
        assertThat(GuavaBiMapWrapper(mutableBiMapOf<Int, String?>(1 to null)),
            equalTo(GuavaBiMapWrapper(mutableBiMapOf<Int, String?>(1 to null))))
        assertThat(GuavaBiMapWrapper(mutableBiMapOf<Int?, String?>(null to null)),
            !equalTo(GuavaBiMapWrapper(mutableBiMapOf<Int?, String?>())))
        assertThat({ guavaBiMap.forcePut(null, "") }, throws<NullPointerException>())
        assertThat({ guavaBiMap.forcePut(1, null) }, throws<NullPointerException>())
        assertThat({ guavaBiMap.forcePut(null, null) }, throws<NullPointerException>())
        assertNull(guavaBiMap.forcePut(1, ""))
        assertThat({ guavaBiMap[null] = "" }, throws<NullPointerException>())
        assertThat({ guavaBiMap[1] = null }, throws<NullPointerException>())
        assertThat({ guavaBiMap[null] = null }, throws<NullPointerException>())
    }
})
