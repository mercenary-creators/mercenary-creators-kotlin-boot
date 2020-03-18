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

package co.mercenary.creators.kotlin.boot

import co.mercenary.creators.kotlin.util.*
import org.reactivestreams.Publisher
import org.springframework.context.*
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder
import reactor.core.publisher.*

@IgnoreForSerialize
abstract class AbstractApplicationSupport : Logging(), ApplicationContextAware {

    private lateinit var application: ApplicationContext

    private val clients = AtomicHashMap<String, WebClient>()

    override fun setApplicationContext(context: ApplicationContext) {
        application = context
    }

    val context: ApplicationContext
        @IgnoreForSerialize
        get() = application

    fun WebClient.RequestHeadersUriSpec<*>.path(path: String): WebClient.RequestHeadersSpec<*> {
        return if (path.isEmpty()) this else uri(path)
    }

    fun WebClient.RequestBodyUriSpec.path(path: String): WebClient.RequestBodySpec {
        return if (path.isEmpty()) this else uri(path)
    }

    inline fun <reified T : Any> typeOf() = object : ParameterizedTypeReference<T>() {}

    @JvmOverloads
    inline fun <reified T : Any> getMonoOf(base: String, path: String = EMPTY_STRING): Mono<T> {
        return getWebClient(base).get().path(path).retrieve().bodyToMono(typeOf())
    }

    @JvmOverloads
    inline fun <reified T : Any> getFluxOf(base: String, path: String = EMPTY_STRING): Flux<T> {
        return getWebClient(base).get().path(path).retrieve().bodyToFlux(typeOf())
    }

    @JvmOverloads
    inline fun <reified T : Any> put(base: String, body: Any, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Int {
        return getWebClient(base).put().path(path).contentType(type).bodyValue(body).exchange().block()!!.rawStatusCode()
    }

    @JvmOverloads
    inline fun <reified T : Any, reified B : Publisher<B>> put(base: String, body: B, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Int {
        return getWebClient(base).put().path(path).contentType(type).body(body, typeOf<B>()).exchange().block()!!.rawStatusCode()
    }

    @JvmOverloads
    inline fun <reified T : Any> post(base: String, body: Any, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Mono<T> {
        return getWebClient(base).post().path(path).contentType(type).bodyValue(body).retrieve().bodyToMono(typeOf())
    }

    @JvmOverloads
    inline fun <reified T : Any, reified B : Publisher<B>> post(base: String, body: B, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Mono<T> {
        return getWebClient(base).post().path(path).contentType(type).body(body, typeOf<B>()).retrieve().bodyToMono(typeOf())
    }

    fun getWebClient(base: String): WebClient {
        return clients.computeIfAbsent(base) { path ->
            WebClient.builder().baseUrl(path).customize().build()
        }
    }

    fun Builder.customize(): Builder {
        return customizeBuilderOf().invoke(this.defaultHeaders(customizeHeadersOf()))
    }

    open fun customizeHeadersOf(): (HttpHeaders) -> Unit = { }

    open fun customizeBuilderOf(): (Builder) -> Builder = { self -> self }

    fun getEnvironmentProperty(name: String): String? = context.environment.getProperty(name)

    fun getEnvironmentProperty(name: String, other: String): String = context.environment.getProperty(name, other)

    fun getEnvironmentProperty(name: String, other: () -> String): String = context.environment.getProperty(name) ?: other()

    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String): T? = context.environment.getProperty(name, T::class.java)

    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String, other: T): T = context.environment.getProperty(name, T::class.java, other)

    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String, other: () -> T): T = context.environment.getProperty(name, T::class.java) ?: other()
}