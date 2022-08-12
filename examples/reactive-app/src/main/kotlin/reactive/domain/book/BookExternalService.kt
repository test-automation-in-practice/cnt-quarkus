package reactive.domain.book

import io.smallrye.mutiny.Uni

interface BookExternalService {

    fun findBookByISBN(isbn: String): Uni<Book>
}
