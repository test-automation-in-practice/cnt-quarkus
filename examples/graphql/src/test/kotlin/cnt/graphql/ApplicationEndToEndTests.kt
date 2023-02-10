package cnt.graphql

import cnt.graphql.business.Movie
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
class ApplicationEndToEndTests {

    @Inject
    @GraphQLClient("movie-test-client")
    private lateinit var dynamicClient: DynamicGraphQLClient

    @Test
    fun `create a movie and retrieve it afterwards`() {
        val createQuery = document(
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
        val createdMovie = dynamicClient.executeSync(createQuery).getObject(Movie::class.java, "createMovie")
        assertMovie(createdMovie, createdMovie.id)

        val retrieveQuery = document(
            operation(
                field(
                    "movie",
                    args(arg("id", createdMovie.id)),
                    field("id"),
                    field("title"),
                    field("director"),
                    field("publishYear")
                )
            )
        )
        val retrievedMovie = dynamicClient.executeSync(retrieveQuery).getObject(Movie::class.java, "movie")
        assertMovie(retrievedMovie, retrievedMovie.id)
    }

    private fun assertMovie(movie: Movie?, id: UUID?) {
        assertThat(movie).isNotNull
        with(movie!!) {
            assertThat(id).isEqualTo(id)
            assertThat(title).isEqualTo("Inception")
            assertThat(director).isEqualTo("Christopher Nolan")
            assertThat(publishYear).isEqualTo(2010)
        }
    }
}