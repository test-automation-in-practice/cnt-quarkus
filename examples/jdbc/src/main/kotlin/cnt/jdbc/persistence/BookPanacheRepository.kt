package cnt.jdbc.persistence

import cnt.jdbc.model.Book
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

@ApplicationScoped
@Transactional
class BookPanacheRepository : PanacheRepository<BookEntity> {

    /**
     * Another way is to use the entityManager to execute a native query.
     * Remember: the EntityManager comes from PanacheRepository which is included in the quarkus-hibernate-orm-panache-kotlin dependency
     */
    fun findBookByIsbn(isbn: String): BookEntity? {
        return getEntityManager().createNativeQuery("select * from book where isbn = ?", BookEntity::class.java)
            .setParameter(1, isbn)
            .singleResult as? BookEntity
    }


    fun getAllBooks(): List<Book> {
        return list("select b from book b").map { Book(it.isbn, it.title) }
    }

    /**
     * As Quarkus does not provide an explicit jdbcTemplate like spring does
     * a compromise would be using the panache build-in functions which allow some native sql snippets.
     */
    fun deleteByIsbn(isbn: String): Long {
        return delete("delete from book b where b.isbn = ?1", isbn)
    }
}
