package reactive.port.rest

import io.mockk.every
import io.mockk.mockk
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber
import org.hibernate.HibernateException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactive.domain.book.BookExternalService
import reactive.domain.book.BookInternalService
import reactive.domain.book.TestBook
import reactive.subscribeAssert
import reactive.subscribeRestResponseAssert
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isTrue

@Suppress("ReactiveStreamsUnusedPublisher")
internal class BookRestControllerTest {

    private val bookInternalService: BookInternalService = mockk()
    private val bookExternalService: BookExternalService = mockk()

    private val cut = BookRestController(bookInternalService, bookExternalService)

    @Nested
    inner class GetAllBooks {

        @Test
        fun `no books exist - return empty list`() {
            every { bookInternalService.getAllBooks() } returns Uni.createFrom().nullItem()

            cut.getAllBooks()
                .subscribeAssert()
                .assertSubscribed()
                .assertCompleted()
        }

        @Test
        fun `books exists - return all books`() {
            val book1 = TestBook.get()
            val book2 = TestBook.get()
            every { bookInternalService.getAllBooks() } returns Uni.createFrom().item(listOf(book1, book2))

            cut.getAllBooks()
                .subscribeAssert()
                .assertCompleted()
                .assertItem(listOf(book1, book2))
        }
    }

    @Nested
    inner class GetBookById {

        @Test
        fun `book does not exist - return 404`() {
            every { bookInternalService.getBookById(1L) } returns Uni.createFrom().nullItem()

            val response = cut
                .getBookById(1L)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())

            val extractedResponse = response.assertCompleted().item

            expectThat(extractedResponse) {
                get { status }.isEqualTo(404)
                get { hasEntity() }.isFalse()
            }
        }

        @Test
        fun `book does exist - return 200`() {
            every { bookInternalService.getBookById(1L) } returns Uni.createFrom().item(TestBook.get())

            val response = cut
                .getBookById(1L)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())

            val extractedResponse = response.assertCompleted().item

            expectThat(extractedResponse) {
                get { status }.isEqualTo(200)
                get { entity?.title }.isNotNull()
                get { entity?.id }.isNotNull().isNotEqualTo(0)
            }
        }

        @Test
        fun `unexpected error - will throw exception`() {
            every { bookInternalService.getBookById(1L) } returns Uni
                .createFrom()
                .failure(HibernateException("Something went unexpectedly wrong"))

            val response = cut
                .getBookById(1L)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())

            response.assertFailedWith(HibernateException::class.java)
        }
    }

    @Nested
    inner class SearchBooksByTitleLike {

        @Test
        fun `do not find any book - return empty list`() {
            every { bookInternalService.findByTitleLike(any()) } returns Uni.createFrom().nullItem()

            cut.searchBooksByTitleLike("Quarkus")
                .subscribeAssert()
                .assertCompleted()
        }

        @Test
        fun `find two books - return 2 books`() {
            val book1 = TestBook.get()
            val book2 = TestBook.get()
            every { bookInternalService.findByTitleLike(any()) } returns Uni.createFrom().item(listOf(book1, book2))

            cut.searchBooksByTitleLike("Quarkus")
                .subscribeAssert()
                .assertCompleted()
                .assertItem(listOf(book1, book2))
        }
    }

    @Nested
    inner class GetBookByIsbn {

        @Test
        fun `get book - return book`() {
            val book = TestBook.get()
            every { bookExternalService.findBookByISBN(book.isbn) } returns Uni.createFrom().item(book)

            val response = cut.getBookByIsbn(book.isbn)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()
                .item

            expectThat(response) {
                get { hasEntity() }.isTrue()
                get { entity }.get { this?.isbn }.isEqualTo(book.isbn)
            }
        }

        @Test
        fun `book does not exist - return 404`() {
            val book = TestBook.get()
            every { bookExternalService.findBookByISBN(book.isbn) } returns Uni.createFrom().nullItem()

            val response = cut.getBookByIsbn(book.isbn)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()
                .item

            expectThat(response) {
                get { hasEntity() }.isFalse()
            }
        }
    }

    @Nested
    inner class CreateNewBook {

        @Test
        fun `create book - return 201`() {
            val book = TestBook.get()
            every { bookInternalService.createBook(book) } returns Uni.createFrom().item(book)

            val response = cut.createNewBook(book)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()
                .item

            expectThat(response) {
                get { status }.isEqualTo(201)
                get { getHeaderString("Location") }.isEqualTo("/books/${book.id}")
            }
        }
    }

    @Nested
    inner class UpdateBookById {

        @Test
        fun `update book - return 200`() {
            val book = TestBook.get()
            every { bookInternalService.updateBook(book) } returns Uni.createFrom().item(book)

            val response = cut.updateBookById(book)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()

            expectThat(response.item.entity).isEqualTo(book)
        }

        @Test
        fun `book does not exist - return 404`() {
            val book = TestBook.get()
            every { bookInternalService.updateBook(book) } returns Uni.createFrom().nullItem()

            val response = cut.updateBookById(book)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()

            expectThat(response.item.status).isEqualTo(404)
        }
    }

    @Nested
    inner class DeleteBookById {

        @Test
        fun `book does not exist - return 404`() {
            every { bookInternalService.deleteBook(1L) } returns Uni.createFrom().item(false)

            val response = cut.deleteBookById(1L)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()

            expectThat(response.item.status).isEqualTo(404)
        }

        @Test
        fun `delete book - return 204`() {
            every { bookInternalService.deleteBook(1L) } returns Uni.createFrom().item(true)

            val response = cut.deleteBookById(1L)
                .subscribeRestResponseAssert()
                .assertSubscribed()
                .assertCompleted()

            expectThat(response.item.status).isEqualTo(204)
        }
    }
}
