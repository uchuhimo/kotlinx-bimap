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
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike
import kotlin.test.assertTrue

object BiMapSpec : SubjectSpek<BiMap<Int, String>>({
    subject { biMapOf(1 to "1", 2 to "2", 3 to "3") }

    itBehavesLike(MapSpec)

    given("""a bimap contains { 1 to "1", 2 to "2", 3 to "3" }""") {
        it("should be equal to another bimap with same content") {
            assertTrue(subject == biMapOf(1 to "1", 2 to "2", 3 to "3"))
        }
        it("should not be equal to another bimap with different content") {
            assertTrue(subject != biMapOf(1 to "1", 2 to "2"))
            assertTrue(subject != biMapOf(1 to "1", 2 to "2", 3 to "4"))
            assertTrue(subject != biMapOf(1 to "1", 2 to "2", 3 to "3", 4 to "4"))
        }
        it("should have same hash code with another bimap with same content") {
            assertThat(subject.hashCode(),
                equalTo(biMapOf(1 to "1", 2 to "2", 3 to "3").hashCode()))
        }
        it("should contain all specified values") {
            assertThat(subject.values, equalTo(setOf("1", "2", "3")))
        }
        on("inverse") {
            it("should map from specified values to specified keys") {
                assertThat(subject.inverse, equalTo(biMapOf("1" to 1, "2" to 2, "3" to 3)))
            }
        }
    }

    given("a bimap with one element") {
        val biMap = biMapOf(1 to "1")

        it("should contain specified key and value") {
            assertThat(biMap, equalTo(biMapOf(1 to "1", 1 to "1")))
        }
    }
})

object ToBiMapSpec : SubjectSpek<BiMap<Int, String>>({
    subject { mapOf(1 to "1", 2 to "2", 3 to "3").toBiMap() }

    itBehavesLike(BiMapSpec)
})
