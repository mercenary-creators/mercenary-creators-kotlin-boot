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
import org.springframework.core.io.Resource
import java.io.InputStream

@IgnoreForSerialize
open class ContentResourceProxy @JvmOverloads @CreatorsDsl constructor(private val base: Resource, path: String = EMPTY_STRING, type: String = DEFAULT_CONTENT_TYPE) : ContentResource {

    private val cache: CachedContentResource by lazy {
        ByteArrayContentResource(getContentData(), getContentPath(), getContentType(), getContentTime(), getContentLook())
    }

    private val name = getResolvedContentPath(base, path)

    private val kind = getResolvedContentType(type, name)

    @CreatorsDsl
    override fun toMapNames(): Map<String, Any?> {
        return dictOf("name" to nameOf(), "path" to getContentPath(), "type" to getContentType(), "time" to getContentTime().toDate())
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentData(): ByteArray {
        return getInputStream().toByteArray()
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentKind() = EMPTY_STRING

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentLook(): ContentResourceLookup {
        return { base.createRelative(it).toContentResource() }
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentMime() = ContentMimeType(getContentType())

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentPath(): String {
        return name
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentSize(): Long {
        return base.contentLength()
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentTime(): Long {
        return base.lastModified()
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getContentType(): String {
        return kind
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getDescription(): String {
        return base.description
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun getInputStream(): InputStream {
        return base.inputStream
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun isContentCache(): Boolean {
        return false
    }

    @CreatorsDsl
    @IgnoreForSerialize
    override fun isContentThere(): Boolean {
        return base.exists()
    }

    @CreatorsDsl
    override fun toContentCache(): CachedContentResource {
        return cache
    }

    companion object {

        @JvmStatic
        @CreatorsDsl
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
        @CreatorsDsl
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