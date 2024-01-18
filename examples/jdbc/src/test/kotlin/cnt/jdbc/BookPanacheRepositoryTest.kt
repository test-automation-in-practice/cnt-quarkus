package cnt.jdbc

import cnt.jdbc.persistence.BookEntity
import cnt.jdbc.persistence.BookPanacheRepository
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.UUID
import javax.inject.Inject
import javax.sql.DataSource

/**
 * Quarkus starts a TestContainer of postgres in the background.
 * So as a developer we don't need to care about setting up a database.
 * Flyway will be executed as well.
 *
 * Quarkus does not provide something like spring does with @DataJpaTest to start only a part of required beans.
 */
@QuarkusTest
class BookPanacheRepositoryTest {

    @Inject
    private lateinit var cut: BookPanacheRepository

    @Inject
    private lateinit var dataSource: DataSource

    @AfterEach
    fun cleanUp() {
        dataSource.connection.use { connection ->
            connection.prepareStatement("delete from book").use { it.executeUpdate() }
        }
    }

    @Test
    fun `find book by isbn`() {
        val randomBookEntity = randomBookEntity()
        cut.persist(randomBookEntity)

        val foundBook = cut.findBookByIsbn(randomBookEntity.isbn)

        assertThat(foundBook)
            .isNotNull
            .extracting { it?.title }.isEqualTo(randomBookEntity.title)
    }

    @Test
    fun `find all persisted books`() {
        cut.persist(randomBookEntity())
        cut.persist(randomBookEntity())
        cut.persist(randomBookEntity())

        val books = cut.getAllBooks()

        assertThat(books).hasSize(3)
    }

    @Test
    fun `delete by isbn`() {
        val randomBook = randomBookEntity().also { cut.persist(it) }

        val result = cut.deleteByIsbn(randomBook.isbn)

        assertThat(result).isEqualTo(1)
        assertThat(cut.getAllBooks()).hasSize(0)
    }

    @Test
    fun `create new book`() {
        cut.persist(randomBookEntity())

        assertThat(cut.getAllBooks()).hasSize(1)
    }

    private fun randomBookEntity(title: String = UUID.randomUUID().toString(), isbn: String = UUID.randomUUID().toString()): BookEntity {
        return BookEntity(isbn, title)
    }
}
