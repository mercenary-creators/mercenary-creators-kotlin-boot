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

package co.mercenary.creators.kotlin.boot.test.make

import co.mercenary.creators.kotlin.boot.*
import co.mercenary.creators.kotlin.util.*
import org.junit.jupiter.api.Test

class ServerTest : KotlinTest() {
    @Test
    fun test() {
        val list = query("SELECT * FROM servers")
        info { list }
        val data = update("DELETE FROM servers")
        info { data }
        MAX_RESILTS.forEach {
            val even = it.isEven().toAtomic()
            val addy = "192.178.255." + Randoms.getInteger(255)
            val node = it.toHexString(8).toUpperCaseEnglish()
            val time = dateOf() + it.seconds + it.milliseconds
            val type = it.toKind()
            val rack = Randoms.getString(10).toUpperCaseEnglish() + "-" + Randoms.getInteger(1..6)
            val many = update("INSERT INTO servers(node,rack,address,boot,installed,type,disks) VALUES(?,?,?,?,?,?,?)", node, rack, addy, Randoms.getBoolean().and(even).isNotTrue(), time, type, Randoms.getInteger(10..15))
            info { many }
        }
        val look = query("SELECT * FROM servers")
        info { look }
    }
}