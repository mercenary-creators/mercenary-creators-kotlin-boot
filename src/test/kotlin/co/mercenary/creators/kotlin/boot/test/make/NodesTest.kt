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

package co.mercenary.creators.kotlin.boot.test.make

import co.mercenary.creators.kotlin.boot.test.KotlinTest
import co.mercenary.creators.kotlin.util.*
import org.junit.jupiter.api.Test
import java.sql.Date

class NodesTest : KotlinTest() {
    @Test
    fun test() {
        val list = query("SELECT * FROM nodes")
        info { list }
        val data = update("DELETE FROM nodes")
        info { data }
        repeat(20) {
            val time = getTimeStamp()
            val type = if (it.rem(2) == 1) "Linux" else "macOS"
            val name = uuid().toUpperCase().replace("-", ".").plus(".wf.domain")
            val many = update("INSERT INTO nodes(name,type,time,active) VALUES(?,?,?,?)", name, type, Date(time), Randoms.getBoolean())
            info { many }
        }
        val look = query("SELECT * FROM nodes")
        info { look }
    }
}