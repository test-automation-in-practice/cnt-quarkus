package cnt.graphql.persistence

import cnt.graphql.business.Movie
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieRepository {

    private val database = mutableMapOf<UUID, Movie>()

    fun save(movie: Movie): Movie {
        val id = movie.id ?: error("id of movie [$movie] was not set!")
        database[id] = movie
        return movie
    }

    fun deleteById(id: UUID) = database.remove(id) != null

    fun getById(id: UUID): Movie? = database[id]

    fun getAll(): List<Movie> = database.values.toMutableList()

    fun deleteAll() = database.clear()
}