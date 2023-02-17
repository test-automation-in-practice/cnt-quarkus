package cnt.adapters.restclient

import cnt.adapters.restclient.model.Actor
import cnt.adapters.restclient.model.Movie
import cnt.adapters.restclient.model.Movies
import cnt.exceptions.MovieNotFoundException
import cnt.exceptions.MovieServiceException
import com.github.tomakehurst.wiremock.WireMockServer
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import util.*
import javax.inject.Inject
import javax.ws.rs.ProcessingException

@QuarkusTest
@TestProfile(WiremockTestProfile::class)
class MovieRestClientTest {

    @Inject
    @RestClient
    lateinit var movieRestClient: MovieRestClient

    @InjectWireMockServer
    lateinit var wireMockServer: WireMockServer

    @AfterEach
    fun resetWireMockServer() {
        wireMockServer.resetAll()
    }

    @Nested
    inner class GetAllMovies {

        @Test
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
            wireMockServer.stubGetMovies(200, responePayload)

            val actualResult = movieRestClient.getAllMovies()

            actualResult assertEqualsComparingFieldByField expectedResult
            wireMockServer.verifyGetMockServerRequest("/movies")
        }

        @Test
        fun `should return emptyList, when Service replies with no movies`() {
            val expectedResult = Movies(
                emptyList()
            )
            val responePayload = """
            {
                "movies": []
            }
            """.trimIndent()
            wireMockServer.stubGetMovies(200, responePayload)

            val actualResult = movieRestClient.getAllMovies()

            actualResult assertEqualsComparingFieldByField expectedResult
            wireMockServer.verifyGetMockServerRequest("/movies")
        }

        @Test
        fun `should throw MovieServiceException, when Service replies with resultCode 500`() {
            wireMockServer.stubGetMovies(500)

            assertThrows<MovieServiceException> {
                movieRestClient.getAllMovies()
            }
            wireMockServer.verifyGetMockServerRequest("/movies")
        }

        @Test
        fun `should throw TimeoutException, when a read timeout occurs`() {
            wireMockServer.stubTimeout("/movies")

            assertThrows<ProcessingException> {
                movieRestClient.getAllMovies()
            }
            wireMockServer.verifyGetMockServerRequest("/movies")
        }
    }

    @Nested
    inner class GetMovieById {

        @Test
        fun `should get Movie by Id, when getMovieById Method is called`() {

            val expectedResult = Movie(
                "1", "Star Wars Episode IV - A New Hope", 1977, listOf(
                    Actor("1", "Marc Hamill", "Luke Skywalker"),
                    Actor("2", "Carrie Fisher", "Leia Organa")
                )
            )
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
            wireMockServer.stubGetMovieById("1", 200, responePayload)

            val actualResult = movieRestClient.getMovieById("1")

            actualResult assertEqualsComparingFieldByField expectedResult
            wireMockServer.verifyGetMockServerRequest("/movie/1")
        }

        @Test
        fun `should throw MovieNotFoundException, when Service replies with resultCode 404`() {
            wireMockServer.stubGetMovieById("1", 404)

            assertThrows<MovieNotFoundException> {
                movieRestClient.getMovieById("1")
            }
            wireMockServer.verifyGetMockServerRequest("/movie/1")
        }

        @Test
        fun `should throw MovieServiceException, when Service replies with resultCode 500`() {
            wireMockServer.stubGetMovieById("1", 500)

            assertThrows<MovieServiceException> {
                movieRestClient.getMovieById("1")
            }
            wireMockServer.verifyGetMockServerRequest("/movie/1")
        }

        @Test
        fun `should throw TimeoutException, when a read timeout occurs`() {
            wireMockServer.stubTimeout("/movie/1")

            assertThrows<ProcessingException> {
                movieRestClient.getMovieById("1")
            }
            wireMockServer.verifyGetMockServerRequest("/movie/1")
        }

    }

    @Nested
    inner class AddMovie {

        private val requestBody = """
            {
                "id" : null,
                "title": "Star Wars Episode IV - A New Hope",
                "publishYear": 1977,
                "cast" : [ ]
            }
            """.trimIndent()

        private val movieToAdd = Movie(
            title = "Star Wars Episode IV - A New Hope",
            publishYear = 1977
        )

        @Test
        fun `should add a Movie and return it with id, when addMovie method is called`() {
            val expectedMovie = Movie(
                id = "1",
                title = "Star Wars Episode IV - A New Hope",
                publishYear = 1977
            )
            val responseBody = """
            {
                "id": "1",
                "title": "Star Wars Episode IV - A New Hope",
                "publishYear": 1977,
                "cast" : [ ]
            }""".trimIndent()
            wireMockServer.stubAddMovie(200, requestBody, responseBody)

            val actualMovie = movieRestClient.addMovie(movieToAdd)

            actualMovie assertEqualsComparingFieldByField expectedMovie
            wireMockServer.verifyPostMockServerRequest("/movie", requestBody)
        }

        @Test
        fun `should throw MovieServiceException, when Service replies with resultCode 500`() {
            wireMockServer.stubAddMovie(500, requestBody)

            assertThrows<MovieServiceException> {
                movieRestClient.addMovie(movieToAdd)
            }
            wireMockServer.verifyPostMockServerRequest("/movie", requestBody)
        }

        @Test
        fun `should throw TimeoutException, when a read timeout occurs`() {
            wireMockServer.stubTimeout("/movie")

            assertThrows<ProcessingException> {
                movieRestClient.addMovie(movieToAdd)
            }
            wireMockServer.verifyPostMockServerRequest("/movie", requestBody)
        }
    }
}