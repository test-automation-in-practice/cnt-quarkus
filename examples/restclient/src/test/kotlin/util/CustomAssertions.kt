package util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.RecursiveComparisonAssert

infix fun <T> T.assertEqualsComparingFieldByField(expected: T): RecursiveComparisonAssert<*> =
    assertThat(this)
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(expected)