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

@file:kotlin.jvm.JvmName("TestKt")

package co.mercenary.creators.kotlin.boot

import co.mercenary.creators.kotlin.util.*

typealias KotlinTest = co.mercenary.creators.kotlin.boot.test.util.AbstractApplicationTests

@CreatorsDsl
const val MAX_RESILTS = 15

@CreatorsDsl
fun Int.toKind(): String {
    return when (abs().minOf(MAX_RESILTS)) {
        in (4..6) -> "macOS"
        in (7..9) -> "Windows"
        else -> "Linux"
    }
}


