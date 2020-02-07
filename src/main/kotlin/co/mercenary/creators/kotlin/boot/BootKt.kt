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
import co.mercenary.creators.kotlin.util.io.*
import co.mercenary.creators.kotlin.util.time.TimeAndDate
import org.springframework.boot.runApplication
import org.springframework.core.io.*

inline fun <reified T : Any> boot(vararg args: String) = runApplication<T>(*args) {
    Encoders
    LoggingFactory
    TimeAndDate.setDefaultTimeZone()
}

fun Resource.toContentResource() = when (val base = this) {
    is WritableResource -> object : ContentResourceProxy(base), OutputContentResource {
        override fun getOutputStream() = base.outputStream
    }
    is AbstractFileResolvingResource -> if (base.isFile) base.file.toContentResource() else base.url.toContentResource()
    is ByteArrayResource -> base.inputStream.toByteArray().let { ByteArrayContentResource(it, Encoders.hex().encode(it)) }
    is InputStreamResource -> base.inputStream.toByteArray().let { ByteArrayContentResource(it, Encoders.hex().encode(it)) }
    else -> ContentResourceProxy(base)
}