package cnt.adapters.restclient

import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.annotations.PactDirectory
import cnt.adapters.restclient.model.Actor
import cnt.adapters.restclient.model.Movie
import cnt.adapters.restclient.model.Movies
import cnt.exceptions.MovieNotFoundException
import cnt.exceptions.MovieServiceException
import io.quarkus.test.junit.QuarkusTest
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import util.*
import javax.inject.Inject

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(providerName = "movies", port = "8081", )
@PactDirectory("src/test/pacts")
@QuarkusTest
class MovieRestClientPactTest {

    @Inject
    @RestClient
    lateinit var movieRestClient: MovieRestClient

    @Nested
    inner class GetAllMovies {

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactGetAllMovies(builder: PactDslWithProvider): V4Pact {
            val headers = mutableMapOf("Content-Type" to "application/json")

            val responePayload = """
            {
                "movies": [
                    {
                        "id": "1",
                        "title": "Star Wars Episode IV - A New Hope",
                        "publishYear": 1977,
                        "cast": [
                            {
                                "id": "1",
                                "name": "Marc Hamill",
                                "inAs": "Luke Skywalker"
                            },
                            {
                                "id": "2",
                                "name": "Carrie Fisher",
                                "inAs": "Leia Organa"
                            }
                        ]
                    },
                    {
                        "id": "2",
                        "title": "Rogue One: A Star Wars Story",
                        "publishYear": 2016,
                        "cast": [
                            {
                                "id": "3",
                                "name": "Felicity Jones",
                                "inAs": "Jyn Erso"
                            }
                        ]
                    }
                ]
            }
            """.trimIndent()

            return builder.given("test GET movies")
                .uponReceiving("GET REQUEST /movies")
                .path("/movies")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responePayload)
                .toPact(V4Pact::class.java)
        }

        @Test
        @PactTestFor(pactMethod = "createPactGetAllMovies")
        fun `should get all Movies, when getAllMovies Method is called`() {

            val expectedResult = Movies(
                listOf(
                    Movie(
                        "1", "Star Wars Episode IV - A New Hope", 1977, listOf(
                            Actor("1", "Marc Hamill", "Luke Skywalker"),
                            Actor("2", "Carrie Fisher", "Leia Organa")
                        )
                    ),
                    Movie(
                        "2", "Rogue One: A Star Wars Story", 2016, listOf(
                            Actor("3", "Felicity Jones", "Jyn Erso")
                        )
                    )
                )
            )

            val actualResult = movieRestClient.getAllMovies()

            actualResult assertEqualsComparingFieldByField expectedResult
        }

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactGetAllMoviesEmpty(builder: PactDslWithProvider): V4Pact {
            val headers = mutableMapOf("Content-Type" to "application/json")

            val responePayload = """
            {
                "movies": [
                ]
            }
            """.trimIndent()
            return builder.given("test GET movies empty")
                .uponReceiving("GET REQUEST /movies empty")
                .path("/movies")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responePayload)
                .toPact(V4Pact::class.java)
        }

        @Test
        @PactTestFor(pactMethod = "createPactGetAllMoviesEmpty")
        fun `should return emptyList, when Service replies with no movies`() {
            val expectedResult = Movies(
                emptyList()
            )

            val actualResult = movieRestClient.getAllMovies()

            actualResult assertEqualsComparingFieldByField expectedResult
        }

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactGetAllMoviesServerError(builder: PactDslWithProvider): V4Pact =
            builder.given("test GET movies internal error")
                .uponReceiving("GET REQUEST /movies Internal Error")
                .path("/movies")
                .method("GET")
                .willRespondWith()
                .status(500)
                .toPact(V4Pact::class.java)

        @Test
        @PactTestFor(pactMethod = "createPactGetAllMoviesServerError")
        fun `should throw MovieServiceException, when Service replies with resultCode 500`() {

            assertThrows<MovieServiceException> {
                movieRestClient.getAllMovies()
            }
        }

// you can't test timeouts with PACT, because PACT is not designed for it
//        @Test
//        fun `should throw TimeoutException, when a read timeout occurs`() {
//            assertThrows<ProcessingException> {
//                movieRestClient.getAllMovies()
//            }
//        }
    }

    @Nested
    inner class GetMovieById {

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactGetMovieById(builder: PactDslWithProvider): V4Pact {
            val headers = mutableMapOf("Content-Type" to "application/json")

            val responePayload = """
            {
                "id": "1",
                "title": "Star Wars Episode IV - A New Hope",
                "publishYear": 1977,
                "cast": [
                    {
                        "id": "1",
                        "name": "Marc Hamill",
                        "inAs": "Luke Skywalker"
                    },
                    {
                        "id": "2",
                        "name": "Carrie Fisher",
                        "inAs": "Leia Organa"
                    }
                ]
            }
            """.trimIndent()

            return builder.given("test GET movies")
                .uponReceiving("GET REQUEST /movies")
                .path("/movie/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responePayload)
                .toPact(V4Pact::class.java)
        }

        @Test
        @PactTestFor(pactMethod = "createPactGetMovieById")
        fun `should get Movie by Id, when getMovieById Method is called`() {

            val expectedResult = Movie(
                        "1", "Star Wars Episode IV - A New Hope", 1977, listOf(
                            Actor("1", "Marc Hamill", "Luke Skywalker"),
                            Actor("2", "Carrie Fisher", "Leia Organa")
                        )
                    )

            val actualResult = movieRestClient.getMovieById("1")

            actualResult assertEqualsComparingFieldByField expectedResult
        }

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactGetMovieByIdNotFound(builder: PactDslWithProvider): V4Pact =
            builder.given("test GET movie not found")
                .uponReceiving("GET REQUEST /movie/2 not found")
                .path("/movie/2")
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact(V4Pact::class.java)

        @Test
        @PactTestFor(pactMethod = "createPactGetMovieByIdNotFound")
        fun `should throw MovieNotFoundException, when Service replies with resultCode 404`() {

            assertThrows<MovieNotFoundException> {
                movieRestClient.getMovieById("2")
            }
        }

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactGetMovieByIdServerError(builder: PactDslWithProvider): V4Pact =
            builder.given("test GET movieById internal error")
                .uponReceiving("GET REQUEST /movie/3 Internal Error")
                .path("/movie/3")
                .method("GET")
                .willRespondWith()
                .status(500)
                .toPact(V4Pact::class.java)

        @Test
        @PactTestFor(pactMethod = "createPactGetMovieByIdServerError")
        fun `should throw MovieServiceException, when Service replies with resultCode 500`() {

            assertThrows<MovieServiceException> {
                movieRestClient.getMovieById("3")
            }
        }
    }

    @Nested
    inner class AddMovie {

        private val requestBody =
            """{"id":null,"title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[]}"""

        private val movieToAdd = Movie(
            title = "Star Wars Episode IV - A New Hope",
            publishYear = 1977
        )

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactPostMovie(builder: PactDslWithProvider): V4Pact {
            val headers = mutableMapOf("Content-Type" to "application/json")

            val responseBody = """{
                "id": "1",
                "title": "Star Wars Episode IV - A New Hope",
                "publishYear": 1977,
                "cast" : [ ]
            }""".trimIndent()

            return builder.given("test POST movie")
                .uponReceiving("POST REQUEST /movie")
                .path("/movie")
                .body(requestBody)
                .method("POST")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responseBody)
                .toPact(V4Pact::class.java)
        }

        @Test
        @PactTestFor(pactMethod = "createPactPostMovie")
        fun `should add a Movie and return it with id, when addMovie method is called`() {
            val expectedMovie = Movie(
                id = "1",
                title = "Star Wars Episode IV - A New Hope",
                publishYear = 1977
            )

            val actualMovie = movieRestClient.addMovie(movieToAdd)

            actualMovie assertEqualsComparingFieldByField expectedMovie
        }

        @Pact(provider = "movies", consumer = "movieClient")
        fun createPactPostMovieInternalError(builder: PactDslWithProvider): V4Pact =
            builder.given("test POST movie internal error")
                .uponReceiving("POST REQUEST /movie internal error")
                .path("/movie")
                .body(requestBody)
                .method("POST")
                .willRespondWith()
                .status(500)
                .toPact(V4Pact::class.java)

        @Test
        @PactTestFor(pactMethod = "createPactPostMovieInternalError")
        fun `should throw MovieServiceException, when Service replies with resultCode 500`() {

            assertThrows<MovieServiceException> {
                movieRestClient.addMovie(movieToAdd)
            }
        }
    }
}