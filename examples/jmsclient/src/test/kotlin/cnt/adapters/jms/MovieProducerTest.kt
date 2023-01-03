package cnt.adapters.jms

import cnt.adapters.jms.MovieProducer.Companion.MOVIE_SEND_QUEUE_NAME
import cnt.adapters.jms.model.Actor
import cnt.adapters.jms.model.Movie
import io.quarkus.artemis.test.ArtemisTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.jms.ConnectionFactory

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource::class)
class MovieProducerTest {

    @Inject
    lateinit var movieProducer: MovieProducer

    @Inject
    lateinit var connectionFactory: ConnectionFactory

    @Test
    fun `should add a movie to the movie-send queue, when sendMovie is called`() {
        val expectedMovie =
            """{"id":"1","title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[{"id":"1","name":"Marc Hamill","inAs":"Luke Skywalker"},{"id":"2","name":"Carrie Fisher","inAs":"Leia Organa"}]}"""
        val movieToSend = Movie(
            "1",
            "Star Wars Episode IV - A New Hope",
            1977,
            listOf(
                Actor("1", "Marc Hamill", "Luke Skywalker"),
                Actor("2", "Carrie Fisher", "Leia Organa")
            )
        )

        movieProducer.sendMovie(movieToSend)

        val actualMovie =  connectionFactory.createContext().use { context ->
            val destination = context.createQueue(MOVIE_SEND_QUEUE_NAME)
            val consumer = context.createConsumer(destination)
            consumer.receiveBody(String::class.java, 2000L)
        }
        assertThat(actualMovie).isEqualTo(expectedMovie)
    }
}