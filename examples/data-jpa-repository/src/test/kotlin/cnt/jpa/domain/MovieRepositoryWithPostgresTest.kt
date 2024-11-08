package cnt.jpa.domain

import util.TransactionalQuarkusTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import jakarta.inject.Inject

@TransactionalQuarkusTest
class MovieRepositoryWithPostgresTest {

    @Inject
    lateinit var movieRepository: MovieRepository

    @BeforeEach
    fun clearDatabase() {
        movieRepository.deleteAll()
    }

    @Test
    fun `should persist and find a movie when persist and find method called`() {
        val id = 1L
        val movie = Movie(id, "Star Wars Episode IV - A New Hope", 1977)

        movieRepository.persist(movie)

        val foundEntity = movieRepository.findById(id)
        assertThat(foundEntity).isEqualTo(movie)
    }

    @Test
    fun `should return first Movie when search by title`() {

        val movie4 = Movie(1, "Star Wars Episode IV - A New Hope", 1977)
        movieRepository.persist(movie4)
        val movieRougeOne = Movie(2, "Rogue One: A Star Wars Story", 2016)
        movieRepository.persist(movieRougeOne)
        val movie9 = Movie(3, "Star Wars Episode IX: The Rise of Skywalker", 2019)
        movieRepository.persist(movie9)


        val foundEntities = movieRepository.findByTitle("Rogue One: A Star Wars Story")
        assertThat(foundEntities)
            .contains(movieRougeOne)
            .doesNotContainSequence(movie4, movie9)
    }

}