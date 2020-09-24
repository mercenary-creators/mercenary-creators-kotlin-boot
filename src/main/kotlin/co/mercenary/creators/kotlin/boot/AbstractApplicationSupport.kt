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
import org.springframework.http.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.*

@IgnoreForSerialize
abstract class AbstractApplicationSupport : Logging(), ApplicationContextAware {

    private lateinit var application: ApplicationContext

    private val clients = atomicMapOf<String, WebClient>()

    override fun setApplicationContext(context: ApplicationContext) {
        application = context
    }

    val context: ApplicationContext
        @IgnoreForSerialize
        get() = application

    @CreatorsDsl
    fun WebClient.RequestHeadersUriSpec<*>.path(path: String): WebClient.RequestHeadersSpec<*> {
        return if (path.isEmpty()) this else uri(path)
    }

    @CreatorsDsl
    fun WebClient.RequestBodyUriSpec.path(path: String): WebClient.RequestBodySpec {
        return if (path.isEmpty()) this else uri(path)
    }

    @CreatorsDsl
    @JvmOverloads
    inline fun <reified T : Any> getMonoOf(base: String, path: String = EMPTY_STRING): Mono<T> {
        return getWebClient(base).get().path(path).retrieve().bodyToMono(toParameterizedTypeReference())
    }

    @CreatorsDsl
    @JvmOverloads
    inline fun <reified T : Any> getFluxOf(base: String, path: String = EMPTY_STRING): Flux<T> {
        return getWebClient(base).get().path(path).retrieve().bodyToFlux(toParameterizedTypeReference())
    }

    @CreatorsDsl
    @JvmOverloads
    inline fun <reified T : Any> put(base: String, body: Any, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Int {
        return getWebClient(base).put().path(path).contentType(type).bodyValue(body).exchange().block()!!.rawStatusCode()
    }

    @CreatorsDsl
    @JvmOverloads
    inline fun <reified T : Any, reified B : Publisher<B>> put(base: String, body: B, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Int {
        return getWebClient(base).put().path(path).contentType(type).body(body, toParameterizedTypeReference<B>()).exchange().block()!!.rawStatusCode()
    }

    @CreatorsDsl
    @JvmOverloads
    inline fun <reified T : Any> post(base: String, body: Any, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Mono<T> {
        return getWebClient(base).post().path(path).contentType(type).bodyValue(body).retrieve().bodyToMono(toParameterizedTypeReference())
    }

    @CreatorsDsl
    @JvmOverloads
    inline fun <reified T : Any, reified B : Publisher<B>> post(base: String, body: B, path: String = EMPTY_STRING, type: MediaType = MediaType.APPLICATION_JSON): Mono<T> {
        return getWebClient(base).post().path(path).contentType(type).body(body, toParameterizedTypeReference<B>()).retrieve().bodyToMono(toParameterizedTypeReference())
    }

    @CreatorsDsl
    fun getWebClient(base: String): WebClient {
        return clients.computeIfAbsent(base) { path ->
            WebClient.builder().baseUrl(path).customize().build()
        }
    }

    @CreatorsDsl
    fun WebClient.Builder.customize(): WebClient.Builder {
        return customizeBuilderOf().invoke(defaultHeaders(customizeHeadersOf()))
    }

    @CreatorsDsl
    open fun customizeHeadersOf(): (HttpHeaders) -> Unit = { }

    @CreatorsDsl
    open fun customizeBuilderOf(): (WebClient.Builder) -> WebClient.Builder = { self -> self }

    @CreatorsDsl
    fun getEnvironmentProperty(name: String): String? = context.environment.getProperty(name)

    @CreatorsDsl
    fun getEnvironmentProperty(name: String, other: String): String = context.environment.getProperty(name, other)

    @CreatorsDsl
    fun getEnvironmentProperty(name: String, other: () -> String): String = context.environment.getProperty(name) ?: other()

    @CreatorsDsl
    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String): T? = context.environment.getProperty(name, T::class.java)

    @CreatorsDsl
    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String, other: T): T = context.environment.getProperty(name, T::class.java, other)

    @CreatorsDsl
    inline fun <reified T : Any> getEnvironmentPropertyOf(name: String, other: () -> T): T = context.environment.getProperty(name, T::class.java) ?: other()
}