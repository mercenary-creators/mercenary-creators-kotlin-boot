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

import co.mercenary.creators.kotlin.json.module.MercenaryKotlinModule
import co.mercenary.creators.kotlin.util.time.TimeAndDate
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.web.servlet.config.annotation.*
import org.springframework.web.servlet.resource.PathResourceResolver

interface DefaultWebConfig : WebMvcConfigurer {

    fun getDefaultJackson2ObjectMapperBuilderCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
        it.dateFormat(TimeAndDate.getDefaultDateFormat()).timeZone(TimeAndDate.getDefaultTimeZone()).modulesToInstall(MercenaryKotlinModule(), ParameterNamesModule())
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val hand = getDefaultRedourcePatterns().distinct().toTypedArray()
        val locs = getDefaultRedourceLocatios().distinct().toTypedArray()
        registry.addResourceHandler(*hand).addResourceLocations(*locs).setCachePeriod(3600).resourceChain(true).addResolver(PathResourceResolver())
    }

    fun getDefaultRedourceLocatios(): List<String> = listOf("/resources/")

    fun getDefaultRedourcePatterns(): List<String> = listOf("/resources/**")
}