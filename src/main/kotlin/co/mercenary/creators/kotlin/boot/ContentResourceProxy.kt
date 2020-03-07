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
import co.mercenary.creators.kotlin.util.io.*
import com.fasterxml.jackson.annotation.JsonIgnoreType
import org.springframework.core.io.Resource
import java.io.InputStream

@JsonIgnoreType
open class ContentResourceProxy @JvmOverloads constructor(private val base: Resource, path: String = EMPTY_STRING, type: String = DEFAULT_CONTENT_TYPE) : ContentResource {

    private val cache: CachedContentResource by lazy {
        ByteArrayContentResource(getContentData(), getContentPath(), getContentType(), getContentTime(), getContentLook())
    }

    private val name = getResolvedContentPath(base, path)

    private val kind = getResolvedContentType(type, name)

    override fun toMapNames(): Map<String, Any?> {
        return mapOf("name" to javaClass.name, "path" to getContentPath(), "type" to getContentType(), "time" to getContentTime().toDate())
    }

    override fun getContentData(): ByteArray {
        return getInputStream().toByteArray()
    }

    override fun getContentLook(): ContentResourceLookup {
        return { base.createRelative(it).toContentResource() }
    }

    override fun getContentPath(): String {
        return name
    }

    override fun getContentSize(): Long {
        return base.contentLength()
    }

    override fun getContentTime(): Long {
        return base.lastModified()
    }

    override fun getContentType(): String {
        return kind
    }

    override fun getDescription(): String {
        return base.description
    }

    override fun getInputStream(): InputStream {
        return base.inputStream
    }

    override fun isContentCache(): Boolean {
        return false
    }

    override fun isContentThere(): Boolean {
        return base.exists()
    }

    override fun toContentCache(): CachedContentResource {
        return cache
    }

    companion object {

        @JvmStatic
        fun getResolvedContentType(type: String, path: String): String {
            if (type.isDefaultContentType()) {
                val look = getDefaultContentTypeProbe().getContentType(path)
                if (look.isDefaultContentType()) {
                    return path.toCommonContentType()
                }
                return look
            }
            return type.toLowerTrim()
        }

        @JvmStatic
        fun getResolvedContentPath(base: Resource, path: String): String {
            try {
                val name = getPathNormalizedOrElse(base.url.file)
                if (name.isNotEmpty()) {
                    return name
                }
            }
            catch (cause: Throwable) {
                Throwables.thrown(cause)
            }
            return getPathNormalizedOrElse(path)
        }
    }
}