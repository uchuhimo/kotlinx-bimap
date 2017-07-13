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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

object MapSpec : SubjectSpek<Map<Int, String>>({
    subject { mapOf(1 to "1", 2 to "2", 3 to "3") }

    given("""a map contains { 1 to "1", 2 to "2", 3 to "3" }""") {
        it("should have size equal to 3") {
            assertThat(subject.size, equalTo(3))
        }
        it("should not be empty") {
            assertFalse(subject.isEmpty())
        }
        it("should contain all specified keys") {
            assertThat(subject.keys, equalTo(setOf(1, 2, 3)))
        }
        it("should contain all specified values") {
            assertThat(subject.values.toList(), equalTo(listOf("1", "2", "3")))
        }
        it("should contain all specified entries") {
            assertThat(subject.entries.map { it.key to it.value }.toSet(),
                    equalTo(setOf(1 to "1", 2 to "2", 3 to "3")))
        }
        on("get with an existed key") {
            val key = 1
            val value = "1"
            it("should contain the key") {
                assertTrue(subject.containsKey(key))
            }
            it("should return the corresponding value using `get`") {
                assertThat(subject[key], equalTo(value))
            }
            it("should return the corresponding value using `get`") {
                assertThat(subject.getOrDefault(key, "4"), equalTo(value))
            }
        }
        on("get with a non-existed key") {
            val key = 4
            it("should not contain the key") {
                assertFalse(subject.containsKey(key))
            }
            it("should return null using `get`") {
                assertNull(subject[key])
            }
            it("should return the default value using `get`") {
                assertThat(subject.getOrDefault(key, "4"), equalTo("4"))
            }
        }
    }
})