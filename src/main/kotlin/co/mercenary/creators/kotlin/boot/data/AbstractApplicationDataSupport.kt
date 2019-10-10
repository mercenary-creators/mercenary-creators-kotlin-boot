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

package co.mercenary.creators.kotlin.boot.data

import co.mercenary.creators.kotlin.boot.*
import co.mercenary.creators.kotlin.json.*
import co.mercenary.creators.kotlin.util.*
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

abstract class AbstractApplicationDataSupport @JvmOverloads constructor(private var name: String = DEFAULT_JSON_RESULTS_NAME) : AbstractApplicationSupport() {

    @Autowired
    private lateinit var template: JdbcTemplate

    val jdbc: JdbcTemplate
        @JsonIgnore
        get() = template

    var resultsName: String
        @JsonIgnore
        get() = toTrimOrElse(name, DEFAULT_JSON_RESULTS_NAME)
        set(value) {
            name = toTrimOrElse(value, DEFAULT_JSON_RESULTS_NAME)
        }

    val sqlDate: java.sql.Date
        @JsonIgnore
        get() = java.sql.Date(getTimeStamp())

    fun update(sql: String, vararg args: Any?) = json(resultsName to json("update" to jdbc.update(sql, *args)))

    fun queryList(sql: String, vararg args: Any?): List<Map<String, Any?>> = jdbc.queryForList(sql, *args)

    fun query(sql: String, vararg args: Any?) = json(resultsName to queryList(sql, *args))

    inline fun <reified T : Any> queryOf(sql: String, vararg args: Any?) = json(resultsName to queryListOf<T>(sql, *args))

    inline fun <reified T : Any> queryListOf(sql: String, vararg args: Any?): List<T> = queryList(sql, *args).let { if (it.isEmpty()) emptyList() else toDataType(it) }
}