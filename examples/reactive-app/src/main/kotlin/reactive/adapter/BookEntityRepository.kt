package reactive.adapter

import io.quarkus.hibernate.reactive.panache.PanacheQuery
import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.Uni
import reactive.domain.book.Book
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class BookEntityRepository : PanacheRepository<BookEntity> {

    fun findByTitle(title: String): PanacheQuery<BookEntity> = find("title like '$title%'")

    fun update(book: Book): Uni<Int> = update("isbn = ?1, title = ?2 where id = ?3", book.isbn, book.title, book.id)
}
