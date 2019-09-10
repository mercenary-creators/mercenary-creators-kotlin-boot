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

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.context.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Flux
import java.util.concurrent.ConcurrentHashMap

abstract class AbstractApplicationSupport : Logging(), ApplicationContextAware {

    private lateinit var application: ApplicationContext

    private val cachedclients = ConcurrentHashMap<String, WebClient>()

    override fun setApplicationContext(context: ApplicationContext) {
        application = context
    }

    val context: ApplicationContext
        @JsonIgnore
        get() = application

    inline fun <reified T : Any> getWebFlux(client: WebClient): Flux<T> {
        return client.get().retrieve().bodyToFlux()
    }

    inline fun <reified T : Any> getWebFlux(base: String): Flux<T> {
        return getWebFlux(getWebClient(base))
    }

    fun getWebClient(base: String): WebClient {
        return cachedclients.computeIfAbsent(base) {
            WebClient.create(it)
        }
    }

    fun getEnvironmentProperty(name: String): String? = context.environment.getProperty(name)

    fun getEnvironmentProperty(name: String, other: String): String = context.environment.getProperty(name, other)

    fun getEnvironmentProperty(name: String, other: () -> String): String = context.environment.getProperty(name) ?: other()

    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String): T? = context.environment.getProperty(name, T::class.java)

    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String, other: T): T = context.environment.getProperty(name, T::class.java, other)

    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String, other: () -> T): T = context.environment.getProperty(name, T::class.java) ?: other()
}