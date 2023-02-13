package cnt.adapters.jms

import cnt.adapters.jms.MovieConsumer.Companion.MOVIE_RECIEVE_QUEUE_NAME
import cnt.adapters.jms.model.Actor
import cnt.adapters.jms.model.Movie
import io.quarkus.artemis.test.ArtemisTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.jms.ConnectionFactory
import javax.jms.JMSContext

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource::class)
class MovieConsumerTest {

    @Inject
    lateinit var movieConsumer: MovieConsumer

    @Inject
    lateinit var connectionFactory: ConnectionFactory

    @Test
    fun `should read the movie from queue movie-receive, when receiveMovie is called`() {
        val expectedMovie = Movie(
            "1",
            "Star Wars Episode IV - A New Hope",
            1977,
            listOf(
                Actor("1", "Marc Hamill", "Luke Skywalker"),
                Actor("2", "Carrie Fisher", "Leia Organa")
            )
        )
        val movieToReceive =
            """{"id":"1","title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[{"id":"1","name":"Marc Hamill","inAs":"Luke Skywalker"},{"id":"2","name":"Carrie Fisher","inAs":"Leia Organa"}]}"""
        connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE).use { context ->
            val destination = context.createQueue(MOVIE_RECIEVE_QUEUE_NAME)
            val producer = context.createProducer()
            producer.send(destination, movieToReceive)
        }

        val actualMovie = movieConsumer.receiveMovie()

        assertThat(actualMovie).isEqualTo(expectedMovie)
    }
}