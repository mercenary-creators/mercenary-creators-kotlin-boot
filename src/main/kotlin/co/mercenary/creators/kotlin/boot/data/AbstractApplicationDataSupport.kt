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

package co.mercenary.creators.kotlin.boot.data

import co.mercenary.creators.kotlin.boot.*
import co.mercenary.creators.kotlin.json.*
import co.mercenary.creators.kotlin.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate

abstract class AbstractApplicationDataSupport : AbstractApplicationSupport() {

    @Autowired
    private lateinit var template: JdbcTemplate

    protected val jdbc: JdbcTemplate
        @IgnoreForSerialize
        get() = template

    protected val results: String
        @IgnoreForSerialize
        get() = "results"

    protected val updated: String
        @IgnoreForSerialize
        get() = "updated"

    @SQLMarker
    protected fun update(sql: String, vararg args: Any?) = json(results.trim() pair json(updated.trim() pair jdbc.update(sql, *args)))

    @SQLMarker
    protected fun queryList(sql: String, vararg args: Any?): List<Map<String, Any?>> = jdbc.queryForList(sql, *args)

    @SQLMarker
    protected fun query(sql: String, vararg args: Any?) = json(results.trim() pair queryList(sql, *args))

    @SQLMarker
    protected inline fun <reified T : Any> queryOf(sql: String, vararg args: Any?) = json(results.trim() pair queryListOf<T>(sql, *args))

    @SQLMarker
    protected inline fun <reified T : Any> queryListOf(sql: String, vararg args: Any?): List<T> = queryList(sql, *args).let { if (it.isEmpty()) listOf() else it.toDataType() }
}