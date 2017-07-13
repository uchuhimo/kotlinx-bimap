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

object EmptyBiMapSpec : SubjectSpek<BiMap<Int, String>>({
    subject { emptyBiMap() }

    itBehavesLike(EmptyMapSpec)

    given("an empty bimap") {
        it("should be equal to another empty bimap") {
            assertTrue(subject == emptyBiMap<Int, String>())
        }
        it("should have same hash code with another empty bimap") {
            assertThat(subject.hashCode(), equalTo(emptyBiMap<Int, String>().hashCode()))
        }
        it("should contain no value") {
            assertThat(subject.values, equalTo(emptySet()))
        }
        on("inverse") {
            it("should still be an empty bimap") {
                assertThat(subject.inverse, equalTo(emptyBiMap()))
            }
        }
    }
})

object BiMapWithoutParameter : SubjectSpek<BiMap<Int, String>>({
    subject { biMapOf() }

    itBehavesLike(EmptyBiMapSpec)
})
