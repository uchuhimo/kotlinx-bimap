package com.uchuhimo.collections

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isIn
import com.natpryce.hamkrest.sameInstance
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object MutableBiMapSpec : Spek({
    given("an empty mutable bimap") {
        val emptyBiMap = mutableBiMapOf<Int, String>()

        it("should contain 0 element") {
            assertThat(emptyBiMap.isEmpty(), equalTo(true))
        }

        on("inverse") {
            val inverseBiMap = emptyBiMap.inverse
            it("should contain 0 element") {
                assertThat(inverseBiMap.isEmpty(), equalTo(true))
            }
        }
    }

    given("a mutable bimap") {
        val biMap by memoized { mutableBiMapOf(1 to "1", 2 to "2", 3 to "3") }

        it("should contain given number of elements") {
            assertThat(biMap.size, equalTo(3))
        }
        it("should contain given keys") {
            assertThat(1, isIn(biMap.keys))
            assertThat(2, isIn(biMap.keys))
            assertThat(3, isIn(biMap.keys))
        }
        it("should contain given values") {
            assertThat("1", isIn(biMap.values))
            assertThat("2", isIn(biMap.values))
            assertThat("3", isIn(biMap.values))
        }
        it("should map from given key to given value") {
            assertThat(biMap[1], equalTo("1"))
        }
        on("inverse") {
            val inverseBiMap = biMap.inverse
            it("should map from key to value as expected") {
                assertThat(inverseBiMap["1"], equalTo(1))
            }
        }
        on("inverse twice") {
            it("should return itself") {
                assertThat(biMap.inverse.inverse, sameInstance(biMap))
            }
        }
        group("put operation") {
            on("put entry, when both key and value are unbound") {
                val previousValue = biMap.put(4, "4")
                it("should contain specified entry") {
                    assertThat(biMap.containsKey(4), equalTo(true))
                    assertThat(biMap.containsValue("4"), equalTo(true))
                    assertThat(biMap[4], equalTo("4"))
                }
                it("should not remove any existing entry") {
                    assertThat(biMap.keys.size, equalTo(4))
                    assertThat(biMap.values.size, equalTo(4))
                    assertThat(previousValue, absent())
                }
            }
            on("put entry, when key exists, and value is unbound") {
                biMap[3] = "4"
                it("should contain specified entry") {
                    assertThat(biMap.containsKey(3), equalTo(true))
                    assertThat(biMap.containsValue("4"), equalTo(true))
                    assertThat(biMap[3], equalTo("4"))
                }
                it("should remove previous value") {
                    assertThat(biMap.containsValue("3"), equalTo(false))
                }
            }
            on("put entry, when key is unbound, and value exists") {
                it("should throw IllegalArgumentException") {
                    assertThat({ biMap.put(4, "3") }, throws<IllegalArgumentException>())
                }
            }
            on("force put entry, when key is unbound, and value exists") {
                biMap.forcePut(4, "3")
                it("should contain specified entry") {
                    assertThat(biMap.containsKey(4), equalTo(true))
                    assertThat(biMap.containsValue("3"), equalTo(true))
                    assertThat(biMap[4], equalTo("3"))
                }
                it("should remove previous key") {
                    assertThat(biMap.containsKey(3), equalTo(false))
                }
            }
            on("force put entry, when both key and value exist") {
                val previousValue = biMap.forcePut(3, "3")
                it("should be unchanged") {
                    assertThat(biMap[3], equalTo(previousValue))
                }
            }
            on("put multiple entries") {
                biMap.putAll(mapOf(4 to "4", 5 to "5", 6 to "6"))
                it("should contain these entries") {
                    assertThat(biMap[4], equalTo("4"))
                    assertThat(biMap[5], equalTo("5"))
                    assertThat(biMap[6], equalTo("6"))
                }
            }
        }
        group("remove operation") {
            on("remove existing key") {
                biMap.remove(1)
                it("should not contain the specified key") {
                    assertThat(biMap.containsKey(1), equalTo(false))
                }
            }
            on("remove unbound key") {
                it("doesn't contain the specified key before removing") {
                    assertThat(biMap.containsKey(4), equalTo(false))
                }
                biMap.remove(4)
                it("should not contain the specified key after removing") {
                    assertThat(biMap.containsKey(4), equalTo(false))
                }
            }
            on("remove existing entry") {
                biMap.remove(2, "2")
                it("should not contain the specified entry") {
                    assertThat(biMap.containsKey(2), equalTo(false))
                    assertThat(biMap.containsValue("2"), equalTo(false))
                }
            }
            on("remove entry, when key exists, and value is unbound") {
                biMap.remove(3, "4")
                it("should not remove any entry") {
                    assertThat(biMap.containsKey(3), equalTo(true))
                }
            }
        }
        on("clear") {
            biMap.clear()
            it("should be empty") {
                assertThat(biMap.isEmpty(), equalTo(true))
            }
        }
    }
})
