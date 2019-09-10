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

package co.mercenary.creators.kotlin.boot.test.util

import co.mercenary.creators.kotlin.boot.DEFAULT_JSON_RESULTS_NAME
import co.mercenary.creators.kotlin.boot.data.AbstractApplicationDataSupport
import co.mercenary.creators.kotlin.json.JSONStatic
import com.fasterxml.jackson.annotation.JsonIgnore
import org.junit.jupiter.api.*
import org.junit.jupiter.api.function.Executable
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [MainTestConfiguration::class])
@TestPropertySource(properties = ["test.bean.name=mercenary:test"], locations = ["classpath:application.properties"])
abstract class AbstractApplicationTests(results: String = DEFAULT_JSON_RESULTS_NAME) : AbstractApplicationDataSupport(results) {

    @Autowired
    private lateinit var pass: PasswordEncoder

    val encoder: PasswordEncoder
        @JsonIgnore
        get() = pass

    fun toJSONString(data: Any, pretty: Boolean = true): String = JSONStatic.toJSONString(data, pretty)

    fun assertTrue(condition: Boolean) {
        Assertions.assertTrue(condition)
    }

    fun assertFalse(condition: Boolean) {
        Assertions.assertFalse(condition)
    }

    fun assertEquals(expected: Any?, actual: Any?) {
        when (expected) {
            is ByteArray -> {
                when (actual) {
                    is ByteArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is CharArray -> {
                when (actual) {
                    is CharArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is ShortArray -> {
                when (actual) {
                    is ShortArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is IntArray -> {
                when (actual) {
                    is IntArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is LongArray -> {
                when (actual) {
                    is LongArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is FloatArray -> {
                when (actual) {
                    is FloatArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is DoubleArray -> {
                when (actual) {
                    is DoubleArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is BooleanArray -> {
                when (actual) {
                    is BooleanArray -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is Array<*> -> {
                when (actual) {
                    is Array<*> -> assertTrue(expected.contentEquals(actual))
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            is Iterable<*> -> {
                when (actual) {
                    is Iterable<*> -> {
                        if (expected == actual) {
                            return
                        }
                        Assertions.assertEquals(expected.toList(), actual.toList())
                    }
                    null -> fail { NOT_SAME_NULL }
                    else -> fail { NOT_SAME_TYPE }
                }
            }
            else -> Assertions.assertEquals(expected, actual)
        }
    }

    fun assertNotEquals(expected: Any?, actual: Any?) {
        when (expected) {
            is ByteArray -> {
                when (actual) {
                    is ByteArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is CharArray -> {
                when (actual) {
                    is CharArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is ShortArray -> {
                when (actual) {
                    is ShortArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is IntArray -> {
                when (actual) {
                    is IntArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is LongArray -> {
                when (actual) {
                    is LongArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is FloatArray -> {
                when (actual) {
                    is FloatArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is DoubleArray -> {
                when (actual) {
                    is DoubleArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is BooleanArray -> {
                when (actual) {
                    is BooleanArray -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is Array<*> -> {
                when (actual) {
                    is Array<*> -> assertFalse(expected.contentEquals(actual))
                    else -> return
                }
            }
            is Iterable<*> -> {
                when (actual) {
                    is Iterable<*> -> {
                        if (expected == actual) {
                            fail { ARE_SAME_TYPE }
                        }
                        Assertions.assertNotEquals(expected.toList(), actual.toList())
                    }
                    else -> return
                }
            }
            else -> Assertions.assertNotEquals(expected, actual)
        }
    }

    infix fun <T : Any> T?.shouldBe(value: Any?) = assertEquals(value, this)

    infix fun <T : Any> T?.shouldNotBe(value: Any?) = assertNotEquals(value, this)

    fun assumeEach(block: AssumeExecutable.() -> Unit) {
        AssumeExecutable(block).also { it.execute() }
    }

    inner class AssumeExecutable(block: AssumeExecutable.() -> Unit) {

        private val list = arrayListOf<Executable>()

        init {
            block(this)
        }

        fun assumeThat(block: () -> Unit) {
            list += Executable { block() }
        }

        fun execute() {
            if (list.isNotEmpty()) {
                Assertions.assertAll(list)
            }
        }
    }

    companion object {
        private const val NOT_SAME_TYPE = "not same type."
        private const val NOT_SAME_NULL = "not same null."
        private const val ARE_SAME_TYPE = "are same type."
    }
}
