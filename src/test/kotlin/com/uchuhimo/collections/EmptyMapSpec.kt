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
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertTrue

object EmptyMapSpec : SubjectSpek<Map<Int, String>>({
    subject { emptyMap() }

    given("an empty map") {
        it("should have size equal to 0") {
            assertThat(subject.size, equalTo(0))
        }
        it("should be empty") {
            assertTrue(subject.isEmpty())
        }
        it("should contain no key") {
            assertThat(subject.keys, equalTo(emptySet()))
        }
        it("should contain no value") {
            assertThat(subject.values.toList(), equalTo(listOf()))
        }
        it("should contain no entry") {
            assertThat(subject.entries, equalTo(emptySet()))
        }
    }
})
