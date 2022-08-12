package reactive.adapter

import io.mockk.every
import io.mockk.mockk
import io.quarkus.hibernate.reactive.panache.PanacheQuery
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactive.domain.book.Book
import reactive.domain.book.TestBook
import reactive.subscribeAssert
import java.time.Duration

@Suppress("ReactiveStreamsUnusedPublisher")
internal class BookPostgresInternalServiceTest {

    private val panacheQuery: PanacheQuery<BookEntity> = mockk()
    private val bookEntityRepository: BookEntityRepository = mockk()
    private val cut = BookPostgresInternalService(bookEntityRepository)

    private val bookEntity1 = TestBook.getEntity()
    private val bookEntity2 = TestBook.getEntity()

    @Nested
    inner class GetAllBooks {

        @Test
        fun `no books - return emptyList`() {
            every { bookEntityRepository.findAll(any()) } returns panacheQuery
            every { panacheQuery.stream<BookEntity>() } returns Multi.createFrom().empty()

            cut.getAllBooks()
                .subscribeAssert(1)
                .assertCompleted()
                .assertHasNotReceivedAnyItem()
        }

        @Test
        fun `return all books`() {
            every { bookEntityRepository.findAll(any()) } returns panacheQuery
            every { panacheQuery.stream<BookEntity>() } returns Multi.createFrom().items(bookEntity1, bookEntity2)

            cut.getAllBooks()
                .subscribeAssert(2)
                .assertCompleted()
                .assertItems(asBook(bookEntity1), asBook(bookEntity2))
        }

        @Test
        fun `timeout - complete`() {
            val multi = Multi.createFrom().empty<BookEntity>()
            val delayedUni = Uni.createFrom().nullItem<BookEntity>().onItem().delayIt().by(Duration.ofMillis(11000))
            val delayedMulti = multi.onItem().call { _ ->
                // Delay the emission until the returned uni emits its item
                delayedUni
            }

            every { bookEntityRepository.findAll(any()) } returns panacheQuery
            every { panacheQuery.stream<BookEntity>() } returns delayedMulti

            cut.getAllBooks()
                .subscribeAssert(1)
                .assertCompleted()
                .assertHasNotReceivedAnyItem()
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
            every { bookEntityRepository.findByTitle("Qua") } returns panacheQuery
            every { panacheQuery.stream<BookEntity>() } returns Multi.createFrom().items(bookEntity1, bookEntity2)

            cut.findByTitleLike("Qua")
                .subscribeAssert(2)
                .assertCompleted()
                .assertItems(asBook(bookEntity1), asBook(bookEntity2))
        }

        @Test
        fun `book does not exist - return empty`() {
            every { bookEntityRepository.findByTitle("Qua") } returns panacheQuery
            every { panacheQuery.stream<BookEntity>() } returns Multi.createFrom().empty()

            cut.findByTitleLike("Qua")
                .subscribeAssert(1)
                .assertCompleted()
                .assertHasNotReceivedAnyItem()
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
