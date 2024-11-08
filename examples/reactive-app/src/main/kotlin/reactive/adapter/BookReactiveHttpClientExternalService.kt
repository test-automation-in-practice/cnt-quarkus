package reactive.adapter

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import reactive.configuration.AddDefaultNoArgConstructor
import reactive.domain.book.Book
import reactive.domain.book.BookExternalService
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam

@ApplicationScoped
class BookReactiveHttpClientExternalService(
    @RestClient private val httpClient: HttpClient
) : BookExternalService {

    override fun findBookByISBN(isbn: String): Uni<Book> {
        return httpClient
            .findBookByIsbn("isbn:$isbn")
            .map { booksResponse ->
                booksResponse.items?.firstOrNull()?.let { Book(null, it.volumeInfo.title, isbn) }
            }
    }
}

@Path("/books/v1/volumes")
@RegisterRestClient(configKey = "google-book-api")
interface HttpClient {

    @GET
    fun findBookByIsbn(@QueryParam("q") id: String): Uni<BooksResponse>
}

@AddDefaultNoArgConstructor
data class BooksResponse(
    val items: List<BookResponse>?
)

@AddDefaultNoArgConstructor
data class BookResponse(
    val volumeInfo: VolumeInfo
)

@AddDefaultNoArgConstructor
data class VolumeInfo(
    val title: String
)
