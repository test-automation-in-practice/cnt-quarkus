package reactive.domain.book

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni

interface BookInternalService {

    fun getAllBooks(): Multi<Book>

    fun findByTitleLike(title: String): Multi<Book>

    fun updateBook(book: Book): Uni<Book>

    fun getBookById(id: Long): Uni<Book?>

    fun createBook(book: Book): Uni<Book>

    fun deleteBook(id: Long): Uni<Boolean>
}
