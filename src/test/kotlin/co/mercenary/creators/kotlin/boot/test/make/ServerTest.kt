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

class ServerTest : KotlinTest() {
    @Test
    fun test() {
        val list = query("SELECT * FROM servers")
        info { list }
        val data = update("DELETE FROM servers")
        info { data }
        repeat(20) {
            val time = getTimeStamp()
            val addy = "192.178.255." + Randoms.getInteger(255)
            val type = if (it.rem(2) == 1) "Linux" else "macOS"
            val rack = Randoms.getString(10).toUpperCase().plus("-").plus(Randoms.getInteger(10))
            val many = update("INSERT INTO servers(rack,address,boot,installed,type,disks) VALUES(?,?,?,?,?,?)", rack, addy, Randoms.getBoolean(), Date(time), type, Randoms.getInteger(10).plus(6))
            info { many }
        }
        val look = query("SELECT * FROM servers")
        info { look }
    }
}