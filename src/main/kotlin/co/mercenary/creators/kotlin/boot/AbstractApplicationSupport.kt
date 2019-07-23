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

package co.mercenary.creators.kotlin.boot

import org.springframework.context.*

abstract class AbstractApplicationSupport : AbstractLogging(), ApplicationContextAware {

    private lateinit var application: ApplicationContext

    override fun setApplicationContext(context: ApplicationContext) {
        application = context
    }

    protected val context: ApplicationContext
        get() = application

    fun getEnvironmentProperty(name: String): String? = context.environment.getProperty(name)

    fun getEnvironmentPropertyIrElse(name: String, other: String): String = context.environment.getProperty(name, other)

    fun getEnvironmentPropertyOrElseCall(name: String, other: () -> String): String = getEnvironmentProperty(name) ?: other()
}