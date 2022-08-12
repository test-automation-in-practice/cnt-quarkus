package reactive.port.rest

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import reactive.domain.book.Book
import reactive.domain.book.BookExternalService
import reactive.domain.book.BookInternalService
import java.net.URI
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.*

@Path("/books")
@ApplicationScoped
class BookRestController(
    private val bookInternalService: BookInternalService,
    private val bookExternalService: BookExternalService
) {

    @GET
    fun getAllBooks(): Multi<Book> {
        return bookInternalService.getAllBooks()
    }

    @GET
    @Path("/{id}")
    fun getBookById(
        @RestPath("id") id: Long
    ): Uni<RestResponse<Book?>> {
        return bookInternalService
            .getBookById(id)
            .onItem().ifNotNull().transform { RestResponse.ok(it) }
            .onItem().ifNull().continueWith(RestResponse.notFound())
    }

    @GET
    @Path("/search/{title}")
    fun searchBooksByTitleLike(
        @RestPath("title") title: String
    ): Multi<Book> {
        return bookInternalService.findByTitleLike(title)
    }

    @GET
    @Path("/isbn/{isbn}")
    fun getBookByIsbn(
        @RestPath("isbn") isbn: String
    ): Uni<RestResponse<Book>> {
        return bookExternalService
            .findBookByISBN(isbn)
            .onItem().ifNotNull().transform { RestResponse.ok(it) }
            .onItem().ifNull().continueWith(RestResponse.notFound())
    }

    @POST
    @ReactiveTransactional
    fun createNewBook(
        book: Book
    ): Uni<RestResponse<Book>> {
        return bookInternalService
            .createBook(book)
            .map { URI.create("/books/${it.id}") }
            .map { RestResponse.created(it) }
    }

    @PUT
    @ReactiveTransactional
    fun updateBookById(
        book: Book
    ): Uni<RestResponse<Book>> {
        return bookInternalService
            .updateBook(book)
            .onItem().ifNotNull().transform { RestResponse.ok(it) }
            .onItem().ifNull().continueWith(RestResponse.notFound())
    }

    @DELETE
    @Path("/{id}")
    @ReactiveTransactional
    fun deleteBookById(
        @RestPath("id") id: Long
    ): Uni<RestResponse<Unit>> {
        return bookInternalService
            .deleteBook(id)
            .map { if (it) RestResponse.noContent() else RestResponse.notFound() }
    }
}
