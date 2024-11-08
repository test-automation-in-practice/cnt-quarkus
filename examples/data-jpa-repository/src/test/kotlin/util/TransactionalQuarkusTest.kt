package util

import io.quarkus.test.junit.QuarkusTest
import jakarta.enterprise.inject.Stereotype
import jakarta.transaction.Transactional

@QuarkusTest
@Stereotype
@Transactional
@Target(AnnotationTarget.CLASS)
@Retention
annotation class TransactionalQuarkusTest {
}