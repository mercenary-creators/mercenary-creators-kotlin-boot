/*
 * Copyright (c) 2019, Mercenary Creators Company. All rights reserved.
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

import co.mercenary.creators.kotlin.boot.data.SQL
import co.mercenary.creators.kotlin.boot.test.KotlinTest
import org.junit.jupiter.api.Test

class UsersTest : KotlinTest() {
    @Test
    fun test() {
        @SQL
        val look = "SELECT username, enabled FROM users"
        val list = queryListOf<UserPartialData>(look)
        info { toJSONString(list) }
        assumeEach {
            assumeThat {
                info { 1 }
                list.size shouldBe 4
            }
            assumeThat {
                info { 2 }
                list.filter { it.enabled }.size shouldBe 3
            }
            assumeThat {
                info { 3 }
                (1..3) shouldBe (1..3)
            }
        }
    }
}