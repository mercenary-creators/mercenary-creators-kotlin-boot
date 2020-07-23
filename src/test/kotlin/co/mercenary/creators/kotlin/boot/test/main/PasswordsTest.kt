/*
 * Copyright (c) 2020, Mercenary Creators Company. All rights reserved.
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

package co.mercenary.creators.kotlin.boot.test.main

import co.mercenary.creators.kotlin.boot.*
import co.mercenary.creators.kotlin.util.*
import org.junit.jupiter.api.Test

class PasswordsTest : KotlinTest() {
    @Test
    fun test() {
        val pass = "abc123"
        val nope = "def456"
        val code = encoder.encode(pass).also { info { it } }
        nope shouldNotBe pass
        code shouldNotBe pass
        code shouldNotBe nope
        encoder.matches(pass, code) shouldBe true
        encoder.matches(nope, code) shouldBe false
        encoder.matches(code, code) shouldBe false
        MAX_RESILTS.forEach {
            warn { Randoms.getInteger(1..6) }
        }
    }
}