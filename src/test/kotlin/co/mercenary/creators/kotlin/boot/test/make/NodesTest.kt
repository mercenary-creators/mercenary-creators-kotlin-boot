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
import org.junit.Test

class NodesTest : KotlinTest() {
    @Test
    fun test() {
        val list = query("SELECT * FROM nodes")
        info { list }
        val data = update("DELETE FROM nodes")
        info { data }
        repeat(20) {
            val even = it.rem(2) == 0
            val node = it.toString(16).toUpperCase().padStart(8, '0')
            val time = getTimeStamp().plus(seconds(it).plus(milliseconds(it))).toDate()
            val type = if (even) "Linux" else if (Randoms.getBoolean()) "macOS" else "Windows"
            val host = uuid().toUpperCase().replace("-", ".").plus(".${node}.dcam.wellsfargo.com")
            val many = update("INSERT INTO nodes(name,host,type,time,active) VALUES(?,?,?,?,?)", node, host, type, time, Randoms.getBoolean().and(even).not())
            info { many }
        }
        val look = query("SELECT * FROM nodes")
        info { look }
    }
}