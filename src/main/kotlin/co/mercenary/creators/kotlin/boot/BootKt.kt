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

@file:kotlin.jvm.JvmName("BootKt")

package co.mercenary.creators.kotlin.boot

import co.mercenary.creators.kotlin.util.*
import org.reactivestreams.Publisher
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.*
import reactor.core.publisher.*
import java.util.concurrent.*
import java.util.stream.*

@CreatorsDsl
const val MAX_RESILTS = 15
fun <T : Any> Flux<T>.toList(): List<T> = collect(Collectors.toList()).block().orElse { emptyList() }

@CreatorsDsl
fun <T : Any> Flux<T>.limit(size: Long): Flux<T> = limitRequest(size)

@CreatorsDsl
fun <T : Any> Flux<T>.limit(size: Int): Flux<T> = limitRequest(size.toLong())

@CreatorsDsl
fun <T : Any> Flux<T>.rated(size: Int): Flux<T> = limitRate(size)

@CreatorsDsl
fun <T : Any> Flux<T>.rated(high: Int, lows: Int): Flux<T> = limitRate(high, lows)

@CreatorsDsl
fun <T : Any> Flux<T>.cache(time: TimeDuration): Flux<T> = cache(time.duration())

@CreatorsDsl
fun <T : Any> Flux<T>.cache(size: Int, time: TimeDuration): Flux<T> = cache(size, time.duration())

@CreatorsDsl
fun <T : Any> Mono<T>.cache(time: TimeDuration): Mono<T> = cache(time.duration())

@CreatorsDsl
fun <T : Any> T.toMono(): Mono<T> = Mono.just(this)

@CreatorsDsl
fun <T> Throwable.toMono(): Mono<T> = Mono.error(this)

@CreatorsDsl
fun <T> Publisher<T>.toMono(): Mono<T> = Mono.from(this)

@CreatorsDsl
fun <T> (() -> T?).toMono(): Mono<T> = Mono.fromSupplier(this)

@CreatorsDsl
fun <T> Callable<T?>.toMono(): Mono<T> = Mono.fromCallable(this::call)

@CreatorsDsl
fun <T> CompletableFuture<out T?>.toMono(): Mono<T> = Mono.fromFuture(this)

@CreatorsDsl
inline fun <reified T : Any> Mono<*>.cast(): Mono<T> {
    return this.cast(T::class.java)
}

@CreatorsDsl
inline fun <reified T : Any> Mono<*>.ofType(): Mono<T> {
    return ofType(T::class.java)
}

@CreatorsDsl
fun <T : Any> Mono<T>.blocked(): T = block() ?: throw MercenaryFatalExceptiion("null Mono.block()")

@CreatorsDsl
fun <T> Throwable.toFlux(): Flux<T> = Flux.error(this)

@CreatorsDsl
fun <T> Array<out T>.toFlux(): Flux<T> = Flux.fromArray(this)

@CreatorsDsl
fun <T : Any> Stream<T>.toFlux(): Flux<T> = Flux.fromStream(this)

@CreatorsDsl
fun <T : Any> Publisher<T>.toFlux(): Flux<T> = Flux.from(this)

@CreatorsDsl
fun <T : Any> Iterable<T>.toFlux(): Flux<T> = Flux.fromIterable(this)

@CreatorsDsl
fun <T : Any> Iterator<T>.toFlux(): Flux<T> = Iterable { this.iterator() }.toFlux()

@CreatorsDsl
fun <T : Any> Sequence<T>.toFlux(): Flux<T> = Iterable { this.iterator() }.toFlux()

@CreatorsDsl
fun IntStream.toFlux(): Flux<Int> = this.boxed().toFlux()

@CreatorsDsl
fun LongStream.toFlux(): Flux<Long> = this.boxed().toFlux()

@CreatorsDsl
fun DoubleStream.toFlux(): Flux<Double> = this.boxed().toFlux()

@CreatorsDsl
fun IntArray.toFlux(): Flux<Int> = this.toList().toFlux()

@CreatorsDsl
fun ByteArray.toFlux(): Flux<Byte> = this.toList().toFlux()

@CreatorsDsl
fun CharArray.toFlux(): Flux<Char> = this.toList().toFlux()

@CreatorsDsl
fun LongArray.toFlux(): Flux<Long> = this.toList().toFlux()

@CreatorsDsl
fun ShortArray.toFlux(): Flux<Short> = this.toList().toFlux()

@CreatorsDsl
fun FloatArray.toFlux(): Flux<Float> = this.toList().toFlux()

@CreatorsDsl
fun DoubleArray.toFlux(): Flux<Double> = this.toList().toFlux()

@CreatorsDsl
fun BooleanArray.toFlux(): Flux<Boolean> = this.toList().toFlux()

@CreatorsDsl
inline fun <reified T : Any> Flux<*>.cast(): Flux<T> {
    return cast(T::class.java)
}

@CreatorsDsl
inline fun <reified T : Any> Flux<*>.ofType(): Flux<T> {
    return ofType(T::class.java)
}

@CreatorsDsl
fun <T : Any> Flux<out Iterable<T>>.split(): Flux<T> = flatMapIterable { it }

@CreatorsDsl
inline fun <reified T : Any> toParameterizedTypeReference() = object : ParameterizedTypeReference<T>() {}

@CreatorsDsl
inline fun <reified T : Any> boot(vararg args: String, init: SpringApplication.() -> Unit = {}): ConfigurableApplicationContext {
    LoggingFactory
    TimeAndDate.setDefaultTimeZone()
    return SpringApplication(T::class.java).apply(init).run(*args)
}

@CreatorsDsl
fun Resource.toContentResource() = when (this) {
    is AbstractFileResolvingResource -> if (isFile) file.toContentResource() else url.toContentResource()
    else -> ContentResourceProxy(this)
}