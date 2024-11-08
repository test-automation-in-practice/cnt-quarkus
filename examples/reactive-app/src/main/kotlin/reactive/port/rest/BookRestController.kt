package reactive.port.rest

import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import reactive.domain.book.Book
import reactive.domain.book.BookExternalService
import reactive.domain.book.BookInternalService
import java.net.URI

@Path("/books")
@ApplicationScoped
class BookRestController(
    private val bookInternalService: BookInternalService,
    private val bookExternalService: BookExternalService
) {

    @GET
    @WithSession
    fun getAllBooks(): Uni<List<Book>> {
        return bookInternalService.getAllBooks()
    }

    @GET
    @Path("/{id}")
    @WithSession
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
    @WithSession
    fun searchBooksByTitleLike(
        @RestPath("title") title: String
    ): Uni<List<Book>> {
        return bookInternalService.findByTitleLike(title)
    }

    @GET
    @Path("/isbn/{isbn}")
    @WithSession
    fun getBookByIsbn(
        @RestPath("isbn") isbn: String
    ): Uni<RestResponse<Book>> {
        return bookExternalService
            .findBookByISBN(isbn)
            .onItem().ifNotNull().transform { RestResponse.ok(it) }
            .onItem().ifNull().continueWith(RestResponse.notFound())
    }

    @POST
    @WithTransaction
    fun createNewBook(
        book: Book
    ): Uni<RestResponse<Book>> {
        return bookInternalService
            .createBook(book)
            .map { URI.create("/books/${it.id}") }
            .map { RestResponse.created(it) }
    }

    @PUT
    @WithTransaction
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
    @WithTransaction
    fun deleteBookById(
        @RestPath("id") id: Long
    ): Uni<RestResponse<Unit>> {
        return bookInternalService
            .deleteBook(id)
            .map { if (it) RestResponse.noContent() else RestResponse.notFound() }
    }
}
