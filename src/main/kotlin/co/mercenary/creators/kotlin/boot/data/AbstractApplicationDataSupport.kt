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

import co.mercenary.creators.kotlin.*
import co.mercenary.creators.kotlin.boot.*
import co.mercenary.creators.kotlin.util.*
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

abstract class AbstractApplicationDataSupport(private var results: String = DEFAULT_JSON_RESULTS_NAME) : AbstractApplicationSupport() {

    @Autowired
    private lateinit var template: JdbcTemplate

    val jdbc: JdbcTemplate
        @JsonIgnore
        get() = template

    var jsonResultsName: String
        @JsonIgnore
        get() = toTrimOrElse(results, DEFAULT_JSON_RESULTS_NAME)
        set(value) {
            results = toTrimOrElse(value, DEFAULT_JSON_RESULTS_NAME)
        }

    fun queryList(@SQL sql: String, vararg args: Any?): List<Map<String, Any?>> = jdbc.queryForList(sql, *args)

    fun query(@SQL sql: String, vararg args: Any?) = json(jsonResultsName to queryList(sql, *args))

    inline fun <reified T : Any> queryOf(@SQL sql: String, vararg args: Any?) = json(jsonResultsName to queryListOf<T>(sql, *args))

    inline fun <reified T : Any> queryListOf(@SQL sql: String, vararg args: Any?): List<T> = queryList(sql, *args).let { if (it.isEmpty()) emptyList() else toDataType(it) }
}