package cnt.jdbc

import cnt.jdbc.model.Book
import cnt.jdbc.persistence.BookDatasourceRepository
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.UUID
import jakarta.inject.Inject
import javax.sql.DataSource

/**
 * Quarkus starts a TestContainer of postgres in the background.
 * So as a developer we don't need to care about setting up a database.
 * Flyway will be executed as well.
 *
 * Quarkus does not provide something like spring does with @DataJpaTest to start only a part of required beans.
 */
@QuarkusTest
class BookDatasourceRepositoryTest {

    @Inject
    private lateinit var bookDatasourceRepository: BookDatasourceRepository

    @Inject
    private lateinit var dataSource: DataSource

    @AfterEach
    fun cleanUp() {
        dataSource.connection.use { connection ->
            connection.prepareStatement("delete from book").use { it.executeUpdate() }
        }
    }

    @Test
    fun `find all persisted books`() {
        bookDatasourceRepository.createBook(randomBook())
        bookDatasourceRepository.createBook(randomBook())
        bookDatasourceRepository.createBook(randomBook())

        val books = bookDatasourceRepository.getAllBooks()

        assertThat(books).hasSize(3)
    }

    @Test
    fun `find book by isbn`() {
        val randomBook = randomBook().also { bookDatasourceRepository.createBook(it) }

        val foundBook = bookDatasourceRepository.findByIsbn(randomBook.isbn)

        assertThat(foundBook)
            .isNotNull
            .extracting { it?.title }.isEqualTo(randomBook.title)
    }

    @Test
    fun `delete by isbn`() {
        val randomBook = randomBook().also { bookDatasourceRepository.createBook(it) }

        val result = bookDatasourceRepository.deleteByIsbn(randomBook.isbn)

        assertThat(result).isEqualTo(1)
        assertThat(bookDatasourceRepository.getAllBooks()).hasSize(0)
    }

    @Test
    fun `create new book`() {
        bookDatasourceRepository.createBook(randomBook())

        assertThat(bookDatasourceRepository.getAllBooks()).hasSize(1)
    }

    private fun randomBook(title: String = UUID.randomUUID().toString(), isbn: String = UUID.randomUUID().toString()): Book {
        return Book(isbn, title)
    }
}
