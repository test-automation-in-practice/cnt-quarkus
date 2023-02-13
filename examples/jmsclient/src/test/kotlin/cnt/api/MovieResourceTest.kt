package cnt.api

import cnt.adapters.jms.MovieConsumer
import cnt.adapters.jms.MovieProducer
import cnt.adapters.jms.model.Movie
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is
import org.junit.jupiter.api.Test

@QuarkusTest
class MovieResourceTest {

    @InjectMock
    lateinit var movieConsumer: MovieConsumer

    @InjectMock
    lateinit var movieProducer: MovieProducer

    @Test
    fun `should get a movie from queue, when getMovie endpoint is called`() {

        every { movieConsumer.receiveMovie() } returns Movie(
            "1",
            "Star Wars Episode IV - A New Hope",
            1977
        )

        RestAssured.given()
            .`when`().get("/movie")
            .then()
            .statusCode(200)
            .body(Is.`is`("""{"id":"1","title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[]}"""))

        verify(exactly = 1) {
            movieConsumer.receiveMovie()
        }
    }

    @Test
    fun `should add a movie to the queue when postMovie endpoint is called`() {
        val expectedMovie = Movie(
            "1",
            "Star Wars Episode IV - A New Hope",
            1977
        )
        justRun { movieProducer.sendMovie(expectedMovie) }

        RestAssured.given()
            .body("""{"id":"1","title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[]}""")
            .contentType(ContentType.JSON)
            .`when`().post("/movie").then()
            .statusCode(201)

        verify(exactly = 1) {
            movieProducer.sendMovie(expectedMovie)
        }
    }
}