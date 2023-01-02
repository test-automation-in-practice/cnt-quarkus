package util

import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Target(FIELD, VALUE_PARAMETER)
@Retention
annotation class InjectWireMockServer()
