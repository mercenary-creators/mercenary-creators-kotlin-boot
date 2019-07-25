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
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Flux
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractApplicationSupport : AbstractLogging(), ApplicationContextAware {

    private lateinit var application: ApplicationContext

    private val cachedclients = ConcurrentHashMap<String, WebClient>()

    override fun setApplicationContext(context: ApplicationContext) {
        application = context
    }

    protected val context: ApplicationContext
        get() = application

    inline fun <reified T : Any> getWebFlux(client: WebClient): Flux<T> {
        return client.get().retrieve().bodyToFlux()
    }

    inline fun <reified T : Any> getWebFlux(base: String): Flux<T> {
        return getWebClient(base).get().retrieve().bodyToFlux()
    }

    fun getWebClient(base: String): WebClient {
        return cachedclients.computeIfAbsent(base) {
            WebClient.create(it)
        }
    }

    fun getEnvironmentProperty(name: String): String? = context.environment.getProperty(name)

    fun getEnvironmentPropertyIrElse(name: String, other: String): String = context.environment.getProperty(name, other)

    fun getEnvironmentPropertyOrElseCall(name: String, other: () -> String): String = getEnvironmentProperty(name) ?: other()
}