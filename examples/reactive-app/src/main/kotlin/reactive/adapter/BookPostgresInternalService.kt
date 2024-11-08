package reactive.adapter

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import reactive.domain.book.Book
import reactive.domain.book.BookInternalService
import java.time.Duration

@ApplicationScoped
class BookPostgresInternalService(
    private val bookEntityRepository: BookEntityRepository
) : BookInternalService {

    override fun getAllBooks(): Uni<List<Book>> {
        // Usually you would get a stream of elements. But with Quarkus 3 they removed the support for streams/multi for the panache repository.
        // For more information see: https://github.com/quarkusio/quarkus/wiki/Migration-Guide-3.0#support-of-multi
        return bookEntityRepository
            .listAll()
            .map { it.map { it.toBook() } }
            .ifNoItem()
            .after(Duration.ofMillis(5000))
            .fail()
            .onFailure()
            .recoverWithNull()
    }

    override fun getBookById(id: Long): Uni<Book?> {
        return bookEntityRepository
            .findById(id)
            .onItem().ifNotNull().transform { it.toBook() }
    }

    override fun findByTitleLike(title: String): Uni<List<Book>> {
        return bookEntityRepository
            .findByTitle(title).list<BookEntity>().map { it.map { it.toBook() } }
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
