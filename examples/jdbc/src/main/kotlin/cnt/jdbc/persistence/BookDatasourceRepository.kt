package cnt.jdbc.persistence

import cnt.jdbc.model.Book
import java.sql.PreparedStatement
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.sql.DataSource
import javax.transaction.Transactional

/**
 * This is a custom CRUD repository for selecting and manipulating book in the database.
 * Quarkus does not support something jdbcTemplate like spring yet. All operations are usually done
 * with the panache repository.
 */
@ApplicationScoped
@Transactional
class BookDatasourceRepository(
    @Inject private val dataSource: DataSource,
) {

    fun getAllBooks(): List<Book> {
        return executeSelectSql("select * from book;")
    }

    fun findByIsbn(isbn: String): Book? {
        return executeSelectSql("select * from book where isbn = ?;") {
            setString(1, isbn)
        }.firstOrNull()
    }

    fun deleteByIsbn(isbn: String): Int {
        return executeManipulatingSql("delete from book where isbn = ?;") {
            setString(1, isbn)
        }
    }

    fun createBook(book: Book): Int {
        return executeManipulatingSql("insert into book (title, isbn) values (?, ?);") {
            setString(1, book.title)
            setString(2, book.isbn)
        }
    }

    /**
     * Executes a reading preparedStatement in an available connection to the database.
     *
     * @param sql which should be executed with placeholder for parameters
     * @param block to replace parameters. The PreparedStatement can be manipulated here.
     */
    private fun executeSelectSql(sql: String, block: PreparedStatement.() -> Unit = {}): List<Book> {
        val books = mutableListOf<Book>()
        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { preparedStatement ->
                block(preparedStatement)
                preparedStatement.executeQuery().use {
                    while (it.next()) {
                        val isbn = it.getString("isbn")
                        val title = it.getString("title")
                        books.add(Book(isbn, title))
                    }
                }
            }
        }
        return books
    }

    /**
     * Executes a manipulating preparedStatement in an available connection to the database.
     *
     * @param sql which should be executed with placeholder for parameters
     * @param block to replace parameters. The PreparedStatement can be manipulated here.
     */
    private fun executeManipulatingSql(sql: String, block: PreparedStatement.() -> Unit = {}): Int {
        return dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use {
                block(it)
                it.executeUpdate()
            }
        }
    }
}
