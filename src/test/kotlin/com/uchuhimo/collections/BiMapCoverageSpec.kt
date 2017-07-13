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

import org.jetbrains.spek.api.Spek
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object BiMapCoverageSpec : Spek({
    test("tests for coverage") {
        assertFalse(equals(biMapOf<Int, String>(), mapOf<Int, String>()))
        assertFalse(equals(biMapOf<Int, String?>(1 to null), biMapOf<Int, String?>(1 to "1")))
        assertFalse(equals(biMapOf<Int, String?>(1 to null), biMapOf<Int, String?>()))
        assertTrue(equals(biMapOf<Int, String?>(1 to null), biMapOf<Int, String?>(1 to null)))
        assertFalse(equals(biMapOf<Int?, String?>(null to null), biMapOf<Int?, String?>()))
    }
})
