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

package co.mercenary.creators.kotlin.boot.test.util

import co.mercenary.creators.kotlin.boot.data.AbstractApplicationDataSupport
import co.mercenary.creators.kotlin.util.*
import co.mercenary.creators.kotlin.util.test.MercenaryMultipleAssertExceptiion
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import kotlin.reflect.KClass

@SpringBootTest(classes = [MainTestConfiguration::class])
@TestPropertySource(properties = ["test.bean.name=mercenary:test"], locations = ["classpath:application.properties"])
abstract class AbstractApplicationTests @JvmOverloads constructor(results: String = "results") : AbstractApplicationDataSupport(results) {

    private val nope = arrayListOf<Class<*>>()

    init {
        Encoders
        addThrowableAsFatal(OutOfMemoryError::class)
        addThrowableAsFatal(StackOverflowError::class)
    }

    @Autowired
    private lateinit var pass: PasswordEncoder

    val encoder: PasswordEncoder
        @JsonIgnore
        get() = pass

    protected val printer: (Int, String) -> Unit = { i, s -> info { "%2d : %s".format(i + 1, s) } }

    fun hours(value: Int): Long = java.util.concurrent.TimeUnit.HOURS.toMillis(value.toLong())

    fun minutes(value: Int): Long = java.util.concurrent.TimeUnit.MINUTES.toMillis(value.toLong())

    fun seconds(value: Int): Long = java.util.concurrent.TimeUnit.SECONDS.toMillis(value.toLong())

    fun milliseconds(value: Int): Long = java.util.concurrent.TimeUnit.MILLISECONDS.toMillis(value.toLong())

    @JvmOverloads
    @AssumptionDsl
    fun dash(loop: Int = 64): String = "-".repeat(loop.abs())

    @AssumptionDsl
    fun uuid(): String = Randoms.uuid()

    @AssumptionDsl
    protected fun fail(text: String): Nothing {
        throw MercenaryAssertExceptiion(text)
    }

    @AssumptionDsl
    protected fun fail(@AssumptionDsl func: () -> Any?): Nothing {
        fail(Formatters.toSafeString(func))
    }

    @AssumptionDsl
    protected fun assertTrueOf(condition: Boolean, @AssumptionDsl func: () -> Any?) {
        if (!condition) {
            fail(func)
        }
    }

    private fun getThrowableOf(@AssumptionDsl func: () -> Unit): Throwable? {
        return try {
            func.invoke()
            null
        }
        catch (oops: Throwable) {
            nope.forEach { type ->
                if (type.isInstance(oops)) {
                    throw oops
                }
            }
            oops
        }
    }

    @AssumptionDsl
    final fun <T : Throwable> addThrowableAsFatal(type: Class<T>) {
        nope += type
    }

    @AssumptionDsl
    final fun <T : Throwable> addThrowableAsFatal(type: KClass<T>) {
        nope += type.java
    }

    @AssumptionDsl
    fun assumeEach(@AssumptionDsl block: AssumeCollector.() -> Unit) {
        AssumeCollector(block).also { it.invoke() }
    }

    @AssumptionDsl
    inner class AssumeCollector(@AssumptionDsl block: AssumeCollector.() -> Unit) {

        private val list = arrayListOf<() -> Unit>()

        init {
            block(this)
        }

        @AssumptionDsl
        fun assumeThat(@AssumptionDsl block: () -> Unit) {
            list += block
        }

        operator fun invoke() {
            if (list.isNotEmpty()) {
                val look = list.mapNotNull { getThrowableOf(it) }
                if (look.isNotEmpty()) {
                    val oops = MercenaryMultipleAssertExceptiion(look)
                    look.forEach {
                        oops.addSuppressed(it)
                    }
                    throw oops
                }
            }
        }
    }

    @AssumptionDsl
    protected infix fun <T : Any?> T.shouldBe(value: T) = assertTrueOf(value isSameAs this) {
        "shouldBe failed"
    }

    @AssumptionDsl
    protected infix fun <T : Any?> T.shouldNotBe(value: T) = assertTrueOf(value isNotSameAs this) {
        "shouldNotBe failed"
    }
}
