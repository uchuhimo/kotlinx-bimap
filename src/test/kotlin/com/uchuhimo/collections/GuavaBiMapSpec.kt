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

import com.google.common.collect.HashBiMap
import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike
import kotlin.test.assertTrue
import com.google.common.collect.BiMap as GuavaBiMap

object GuavaBiMapSpec : SubjectSpek<GuavaBiMap<Int, String>>({
    subject { HashBiMap.create(mapOf(1 to "1", 2 to "2", 3 to "3")) }

    itBehavesLike(MapSpec)

    given("a guava bimap") {
        it("should be equal to another guava bimap with same content") {
            assertTrue(subject == HashBiMap.create(mapOf(1 to "1", 2 to "2", 3 to "3")))
        }
        it("should not be equal to another guava bimap with different content") {
            assertTrue(subject != HashBiMap.create(mapOf(1 to "1", 2 to "2")))
            assertTrue(subject != HashBiMap.create(mapOf(1 to "1", 2 to "2", 3 to "4")))
            assertTrue(subject != HashBiMap.create(mapOf(1 to "1", 2 to "2", 3 to "3", 4 to "4")))
        }
        it("should have same hash code with another guava bimap with same content") {
            assertThat(subject.hashCode(),
                equalTo(HashBiMap.create(mapOf(1 to "1", 2 to "2", 3 to "3")).hashCode()))
        }
        it("should contain all specified values") {
            assertThat(subject.values, equalTo(setOf("1", "2", "3")))
        }
        on("inverse") {
            it("should map from specified values to specified keys") {
                val expected: GuavaBiMap<String, Int> =
                    HashBiMap.create(mapOf("1" to 1, "2" to 2, "3" to 3))
                assertThat(subject.inverse(), equalTo(expected))
            }
        }
        on("inverse twice") {
            it("should be same with itself") {
                assertThat(subject.inverse().inverse(), equalTo(subject))
            }
        }
        group("put operation") {
            on("put entry, when both key and value are unbound") {
                val previousValue = subject.put(4, "4")
                it("should contain specified entry") {
                    assertThat(subject.containsKey(4), equalTo(true))
                    assertThat(subject.containsValue("4"), equalTo(true))
                    assertThat(subject[4], equalTo("4"))
                }
                it("should not remove any existing entry") {
                    assertThat(subject.keys.size, equalTo(4))
                    assertThat(subject.values.size, equalTo(4))
                    assertThat(previousValue, absent())
                }
            }
            on("put entry, when key exists, and value is unbound") {
                subject[3] = "4"
                it("should contain specified entry") {
                    assertThat(subject.containsKey(3), equalTo(true))
                    assertThat(subject.containsValue("4"), equalTo(true))
                    assertThat(subject[3], equalTo("4"))
                }
                it("should remove previous value") {
                    assertThat(subject.containsValue("3"), equalTo(false))
                }
            }
            on("put entry, when key is unbound, and value exists") {
                it("should throw IllegalArgumentException") {
                    assertThat({ subject[4] = "3" }, throws<IllegalArgumentException>())
                }
            }
            on("force put entry, when key is unbound, and value exists") {
                subject.forcePut(4, "3")
                it("should contain specified entry") {
                    assertThat(subject.containsKey(4), equalTo(true))
                    assertThat(subject.containsValue("3"), equalTo(true))
                    assertThat(subject[4], equalTo("3"))
                }
                it("should remove previous key") {
                    assertThat(subject.containsKey(3), equalTo(false))
                }
            }
            on("force put entry, when both key and value exist") {
                val previousValue = subject.forcePut(3, "3")
                it("should be unchanged") {
                    assertThat(subject[3], equalTo(previousValue))
                }
            }
            on("put multiple entries") {
                subject.putAll(mapOf(4 to "4", 5 to "5", 6 to "6"))
                it("should contain these entries") {
                    assertThat(subject[4], equalTo("4"))
                    assertThat(subject[5], equalTo("5"))
                    assertThat(subject[6], equalTo("6"))
                }
            }
        }
        group("remove operation") {
            on("remove existing key") {
                subject.remove(1)
                it("should not contain the specified key") {
                    assertThat(subject.containsKey(1), equalTo(false))
                }
            }
            on("remove unbound key") {
                it("doesn't contain the specified key before removing") {
                    assertThat(subject.containsKey(4), equalTo(false))
                }
                subject.remove(4)
                it("should not contain the specified key after removing") {
                    assertThat(subject.containsKey(4), equalTo(false))
                }
            }
        }
        on("clear") {
            subject.clear()
            it("should be empty") {
                assertThat(subject.isEmpty(), equalTo(true))
            }
        }
    }
})

object MutableBiMapAsGuavaBiMapSpec : SubjectSpek<GuavaBiMap<Int, String>>({
    subject { mutableBiMapOf(1 to "1", 2 to "2", 3 to "3").asGuavaBiMap() }

    itBehavesLike(GuavaBiMapSpec)
})

internal object GuavaBiMapWrapperSpec : SubjectSpek<GuavaBiMapWrapper<Int, String>>({
    subject { GuavaBiMapWrapper(mutableBiMapOf(1 to "1", 2 to "2", 3 to "3")) }

    itBehavesLike(GuavaBiMapSpec)
})

object InverseGuavaBiMapSpec : SubjectSpek<GuavaBiMap<Int, String>>({
    subject { HashBiMap.create(mapOf("1" to 1, "2" to 2, "3" to 3)).inverse() }

    itBehavesLike(GuavaBiMapSpec)
})
