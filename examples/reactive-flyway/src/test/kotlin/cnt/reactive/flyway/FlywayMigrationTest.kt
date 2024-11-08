package cnt.reactive.flyway

import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@QuarkusTest
class FlywayMigrationTest {

    @Inject
    private lateinit var bookRepository: BookRepository

    @Test
    fun `when running tests - use testData inserted by flyway`() {
        val result = bookRepository.getAllEntries().block()

        assertThat(result).hasSize(5)
    }

    private fun <T : Any> Uni<T>.block(): T = this.await().indefinitely()
}