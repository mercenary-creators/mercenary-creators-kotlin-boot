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

import co.mercenary.creators.kotlin.json.module.MercenaryKotlinModule
import co.mercenary.creators.kotlin.util.time.TimeAndDate
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.resource.PathResourceResolver

abstract class AbstractApplicationConfiguration @JvmOverloads constructor(private val list: List<String> = listOf("/resources/"), private val period: Int = 3600) : WebMvcConfigurer {

    @Bean
    open fun bootJackson() = Jackson2ObjectMapperBuilderCustomizer {
        it.dateFormat(TimeAndDate.getDefaultDateFormat()).timeZone(TimeAndDate.getDefaultTimeZone()).modulesToInstall(MercenaryKotlinModule())
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        if (list.isNotEmpty()) {
            val locs = list.map { snap(it) }.filter { it.isNotEmpty() }.distinct().toTypedArray()
            val hand = locs.map { it.plus(STARS).plus(STARS) }.distinct().toTypedArray()
            registry.addResourceHandler(*hand).addResourceLocations(*locs).setCachePeriod(period).resourceChain(true).addResolver(PathResourceResolver())
        }
    }

    companion object {
        private const val SLASH = "/"
        private const val STARS = "*"
        private fun snap(data: String): String {
            var temp = data.trim()
            while (temp.startsWith(SLASH)) {
                temp = temp.removePrefix(SLASH).trim()
            }
            while (temp.endsWith(SLASH)) {
                temp = temp.removeSuffix(SLASH).trim()
            }
            while (temp.endsWith(STARS)) {
                temp = temp.removeSuffix(STARS).trim()
            }
            temp = temp.trim()
            if (temp.isEmpty()) {
                return temp
            }
            return SLASH.plus(temp).plus(SLASH)
        }
    }
}