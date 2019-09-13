package co.mercenary.creators.kotlin.boot.data

import org.intellij.lang.annotations.Language
import kotlin.annotation.AnnotationTarget.*
@Language("SQL")
@Retention(AnnotationRetention.RUNTIME)
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, FIELD, VALUE_PARAMETER, LOCAL_VARIABLE)
annotation class SQL