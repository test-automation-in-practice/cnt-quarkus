package reactive.adapter

import io.mockk.every
import io.mockk.mockk
import io.quarkus.hibernate.reactive.panache.PanacheQuery
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import reactive.domain.book.Book
import reactive.domain.book.TestBook
import reactive.subscribeAssert
import java.time.Duration
import java.util.concurrent.TimeUnit

@Suppress("ReactiveStreamsUnusedPublisher")
internal class BookPostgresInternalServiceTest {

    private val bookEntityRepository: BookEntityRepository = mockk()
    private val cut = BookPostgresInternalService(bookEntityRepository)

    private val bookEntity1 = TestBook.getEntity()
    private val bookEntity2 = TestBook.getEntity()

    @Nested
    inner class GetAllBooks {

        @Test
        fun `no books - return emptyList`() {
            every { bookEntityRepository.listAll() } returns Uni.createFrom().nullItem()

            cut.getAllBooks()
                .subscribeAssert()
                .assertCompleted()
                .assertItem(null)
        }

        @Test
        fun `return all books`() {
            every { bookEntityRepository.listAll() } returns Uni.createFrom().item(listOf(bookEntity1, bookEntity2))

            cut.getAllBooks()
                .subscribeAssert()
                .assertCompleted()
                .assertItem(listOf(asBook(bookEntity1), asBook(bookEntity2)))
        }
    }

    @Nested
    inner class GetBookById {

        @Test
        fun `book does exist - return book`() {
            every { bookEntityRepository.findById(any()) } returns Uni.createFrom().item(bookEntity1)

            cut.getBookById(1L)
                .subscribeAssert()
                .assertCompleted()
                .assertItem(asBook(bookEntity1))
        }

        @Test
        fun `book does not exist - return empty`() {
            every { bookEntityRepository.findById(any()) } returns Uni.createFrom().nullItem()

            cut.getBookById(1L)
                .subscribeAssert()
                .assertCompleted()
                .assertItem(null)
        }
    }

    @Nested
    inner class FindByTitleLike {

        @Test
        fun `books do exists - return books`() {
            every { bookEntityRepository.findByTitle("Qua").list<BookEntity>() } returns Uni.createFrom().item(listOf(bookEntity1, bookEntity2))

            cut.findByTitleLike("Qua")
                .subscribeAssert()
                .assertCompleted()
                .assertItem(listOf(asBook(bookEntity1), asBook(bookEntity2)))
        }

        @Test
        fun `book does not exist - return empty`() {
            every { bookEntityRepository.findByTitle("Qua").list<BookEntity>() } returns Uni.createFrom().nullItem()

            cut.findByTitleLike("Qua")
                .subscribeAssert()
                .assertFailed()
        }
    }

    @Nested
    inner class UpdateBook {

        @Test
        fun `update book - return book`() {
            val book = asBook(bookEntity1)
            every { bookEntityRepository.update(book) } returns Uni.createFrom().item(1)

            cut.updateBook(book)
                .subscribeAssert()
                .assertCompleted()
                .assertItem(book)
        }

        @Test
        fun `book does not exist - return book`() {
            val book = asBook(bookEntity1)
            every { bookEntityRepository.update(book) } returns Uni.createFrom().item(0)

            cut.updateBook(book)
                .subscribeAssert()
                .assertCompleted()
                .assertItem(null)
        }
    }

    @Nested
    inner class CreateBook {

        @Test
        fun `create new book - return persisted book`() {
            every { bookEntityRepository.persist(any<BookEntity>()) } returns Uni.createFrom().item(bookEntity1)

            cut.createBook(asBook(bookEntity1))
                .subscribeAssert()
                .assertCompleted()
                .assertItem(asBook(bookEntity1))
        }
    }

    @Nested
    inner class DeleteBook {

        @Test
        fun `delete book - return true`() {
            every { bookEntityRepository.deleteById(any()) } returns Uni.createFrom().item(true)

            cut.deleteBook(1L)
                .subscribeAssert()
                .assertCompleted()
                .assertItem(true)
        }

        @Test
        fun `do not delete book - return false`() {
            every { bookEntityRepository.deleteById(any()) } returns Uni.createFrom().item(false)

            cut.deleteBook(1L)
                .subscribeAssert()
                .assertCompleted()
                .assertItem(false)
        }
    }

    private fun asBook(bookEntity: BookEntity): Book {
        return Book(bookEntity.id, bookEntity.title, bookEntity.isbn)
    }
}
