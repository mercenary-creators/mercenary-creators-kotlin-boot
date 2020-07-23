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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.security.SecureRandom

@IgnoreForSerialize
class SecurePasswordEncoder @JvmOverloads @CreatorsDsl constructor(strength: Int = DEFAULT_STRENGTH, random: SecureRandom = Randoms.randomOf()) : BCryptPasswordEncoder(strengthOf(strength), random) {

    override fun toString() = nameOf()

    override fun hashCode() = idenOf()

    override fun equals(other: Any?) = when (other) {
        is SecurePasswordEncoder -> this === other
        else -> false
    }

    companion object {

        @CreatorsDsl
        const val MINIMUM_STRENGTH = 4

        @CreatorsDsl
        const val MAXIMUM_STRENGTH = 31

        @CreatorsDsl
        const val DEFAULT_STRENGTH = 12

        @JvmStatic
        @CreatorsDsl
        fun strengthOf(strength: Int): Int {
            return if (strength < 1) DEFAULT_STRENGTH else strength.boxIn(MINIMUM_STRENGTH, MAXIMUM_STRENGTH)
        }
    }
}