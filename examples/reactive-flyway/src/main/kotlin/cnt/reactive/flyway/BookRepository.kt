package cnt.reactive.flyway

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Row
import jakarta.enterprise.context.RequestScoped

@RequestScoped
class BookRepository(
    private val client: PgPool
) {

    /**
     * Returns a list of all books
     */
    fun getAllEntries(): Uni<List<Book>> = client
        .preparedQuery("select id, title, author from book")
        .execute().map { resultSet ->
            resultSet.map { row -> row.toDomain() }
        }

    /**
     * Maps a [Row] to the domain object
     */
    private fun Row.toDomain(): Book = Book(
        id = getUUID("id"),
        title = getString("title"),
        author = getString("author")
    )
}
