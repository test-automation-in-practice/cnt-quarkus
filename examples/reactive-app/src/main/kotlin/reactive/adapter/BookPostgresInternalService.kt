package reactive.adapter

import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import reactive.domain.book.Book
import reactive.domain.book.BookInternalService
import java.time.Duration
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BookPostgresInternalService(
    private val bookEntityRepository: BookEntityRepository
) : BookInternalService {

    private companion object {
        const val ID = "id"
    }

    override fun getAllBooks(): Multi<Book> {
        return bookEntityRepository
            .findAll(Sort.by(ID)).stream<BookEntity>()
            .map { it.toBook() }
            .ifNoItem()
            .after(Duration.ofMillis(10000))
            .fail()
            .onFailure()
            .recoverWithCompletion()
    }

    override fun getBookById(id: Long): Uni<Book?> {
        return bookEntityRepository
            .findById(id)
            .onItem().ifNotNull().transform { it.toBook() }
    }

    override fun findByTitleLike(title: String): Multi<Book> {
        return bookEntityRepository
            .findByTitle(title)
            .stream<BookEntity>()
            .map { it.toBook() }
    }

    override fun updateBook(book: Book): Uni<Book> {
        return bookEntityRepository
            .update(book)
            .onItem().transform { if (it == 0) null else book }
    }

    override fun createBook(book: Book): Uni<Book> {
        return bookEntityRepository
            .persist(book.toBookEntity())
            .map { it.toBook() }
    }

    override fun deleteBook(id: Long): Uni<Boolean> {
        return bookEntityRepository
            .deleteById(id)
    }

    private fun BookEntity.toBook(): Book = Book(id, title, isbn)

    private fun Book.toBookEntity(): BookEntity = BookEntity(null, title, isbn)
}
