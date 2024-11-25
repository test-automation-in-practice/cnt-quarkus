package cnt.graphql.business

import cnt.graphql.persistence.MovieRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieService(
    private val idGenerator: IdGenerator,
    private val movieRepository: MovieRepository
) {

    fun add(movie: Movie): Movie {
        val id = idGenerator.generateId()
        return movieRepository.save(movie.copy(id = id))
    }

    fun getAll(): List<Movie> = movieRepository.getAll()

    fun getSingle(id: UUID): Movie? = movieRepository.getById(id)

    fun delete(id: UUID): Boolean = movieRepository.deleteById(id)

}