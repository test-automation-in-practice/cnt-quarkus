package cnt.graphql.api

import cnt.graphql.business.Movie
import cnt.graphql.business.MovieService
import io.mockk.every
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.graphql.client.GraphQLClient
import io.smallrye.graphql.client.core.Argument.arg
import io.smallrye.graphql.client.core.Argument.args
import io.smallrye.graphql.client.core.Document.document
import io.smallrye.graphql.client.core.Field.field
import io.smallrye.graphql.client.core.InputObject.inputObject
import io.smallrye.graphql.client.core.InputObjectField.prop
import io.smallrye.graphql.client.core.Operation.operation
import io.smallrye.graphql.client.core.OperationType.MUTATION
import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Inject

@QuarkusTest
class MovieResourceTests {

    @Inject
    @GraphQLClient("movie-test-client")
    private lateinit var dynamicClient: DynamicGraphQLClient

    @InjectMock
    private lateinit var service: MovieService

    private val ghostDogId = UUID.fromString("87a9c22d-f07b-47cf-baa8-d48d6cd83c59")

    private val ghostDog = Movie(
        id = ghostDogId,
        title = "Ghost Dog: The Way of the Samurai",
        director = "David Lynch",
        publishYear = 1997
    )

    private val lostHighway = Movie(
        id = UUID.fromString("696829c2-dc75-49ae-a798-0017c9976a93"),
        title = "Lost Highway",
        director = "Jim Jarmusch",
        publishYear = 1999
    )

    private val inception = Movie(
        id = UUID.fromString("4a97db62-e581-4776-80ab-6b4a21927974"),
        title = "Inception",
        director = "Christopher Nolan",
        publishYear = 2010
    )


    @Test
    fun `get all movies`() {
        val allMovies = listOf(ghostDog, lostHighway, inception)
        every { service.getAll() } returns allMovies

        val query = document(
            operation(
                field(
                    "allMovies",
                    field("id"),
                    field("title"),
                    field("director"),
                    field("publishYear")
                )
            )
        )
        val result = dynamicClient.executeSync(query).getList(Movie::class.java, "allMovies")

        assertThat(result).isEqualTo(allMovies)
    }

    @Test
    fun `get specific movie by its ID`() {
        every { service.getSingle(ghostDogId) } returns ghostDog

        val query = document(
            operation(
                field(
                    "movie",
                    args(arg("id", ghostDogId)),
                    field("id"),
                    field("title"),
                    field("director"),
                    field("publishYear")
                )
            )
        )
        val result = dynamicClient.executeSync(query).getObject(Movie::class.java, "movie")

        assertThat(result).isEqualTo(ghostDog)
    }

    @Test
    fun `create and save a new movie`() {
        every { service.add(inception.copy(id = null)) } returns inception

        val query = document(
            operation(
                MUTATION,
                field(
                    "createMovie",
                    args(
                        arg(
                            "movie", inputObject(
                                prop("title", "Inception"),
                                prop("director", "Christopher Nolan"),
                                prop("publishYear", 2010)
                            )
                        )
                    ),
                    field("id"),
                    field("title"),
                    field("director"),
                    field("publishYear")
                )
            )
        )
        val result = dynamicClient.executeSync(query).getObject(Movie::class.java, "createMovie")

        assertThat(result).isEqualTo(inception)
    }

    @Test
    fun `delete a movie by its ID`() {
        every { service.delete(ghostDogId) } returns true

        val query = document(
            operation(
                MUTATION,
                field(
                    "deleteMovie",
                    args(
                        arg("id", ghostDogId)
                    )
                )
            )
        )
        val result = dynamicClient.executeSync(query).getObject(Boolean::class.java, "deleteMovie")

        assertThat(result).isTrue
    }

    @Test
    fun `delete for unknown ID`() {
        every { service.delete(any()) } returns false

        val query = document(
            operation(
                MUTATION,
                field(
                    "deleteMovie",
                    args(
                        arg("id", UUID.randomUUID())
                    )
                )
            )
        )
        val result = dynamicClient.executeSync(query).getObject(Boolean::class.java, "deleteMovie")

        assertThat(result).isFalse
    }
}