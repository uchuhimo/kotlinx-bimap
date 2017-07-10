package com.uchuhimo.collections

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isIn
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object BiMapSpec : Spek({
    given("an empty bimap") {
        val emptyBiMap = emptyBiMap<Int, String>()

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

    given("a bimap without element") {
        val biMap = biMapOf<Int, String>()

        it("should contain 0 element") {
            assertThat(biMap.isEmpty(), equalTo(true))
        }
    }

    given("a bimap with one element") {
        val biMap = biMapOf(1 to "1")

        it("should contain 1 element") {
            assertThat(biMap.size, equalTo(1))
        }
        it("should contain given key") {
            assertThat(biMap.containsKey(1), equalTo(true))
        }
        it("should contain given value") {
            assertThat(biMap.containsValue("1"), equalTo(true))
        }
    }

    given("a bimap with multiple elements") {
        val biMap = biMapOf(1 to "1", 2 to "2", 3 to "3")

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
        it("should map from key to value as expected") {
            assertThat(biMap[1], equalTo("1"))
        }
        on("inverse") {
            val inverseBiMap = biMap.inverse
            it("should map from key to value as expected") {
                assertThat(inverseBiMap["1"], equalTo(1))
            }
        }
    }
})
