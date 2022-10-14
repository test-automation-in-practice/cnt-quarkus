package cnt.mongodb.domain

import io.quarkus.test.junit.QuarkusTest
import javax.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@QuarkusTest
class MovieRepositoryTest {

    @Inject
    lateinit var cut: MovieRepository

    @BeforeEach
    fun clearDatabase() {
        cut.deleteAll()
    }

    @Test
    fun `should persist and find a movie when persist and find method called`() {
        val marc = Actor(ObjectId(), "Marc Hamill", "Luke Skywalker")
        val carrie = Actor(ObjectId(), "Carrie Fisher", "Leia Organa")
        val movie = Movie(ObjectId(), "Star Wars Episode IV - A New Hope", 1977, listOf(marc, carrie))

        cut.persist(movie)

        val resultMovie = cut.findByTitle("Star Wars Episode IV - A New Hope")

        assertThat(resultMovie).isEqualTo(movie)
    }

    @Test
    fun `should persist and find a movie by actor name when persist and find method called`() {
        val marc = Actor(ObjectId(), "Marc Hamill", "Luke Skywalker")
        val carrie = Actor(ObjectId(), "Carrie Fisher", "Leia Organa")
        val movie = Movie(ObjectId(), "Star Wars Episode IV - A New Hope", 1977, listOf(marc, carrie))

        cut.persist(movie)

        val results = cut.findByActorName("Carrie")

        assertThat(results).containsOnly(movie)
    }

}