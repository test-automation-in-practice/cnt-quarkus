package cnt.adapters.jms

import cnt.adapters.jms.model.Movie
import com.fasterxml.jackson.databind.ObjectMapper
import javax.enterprise.context.ApplicationScoped
import javax.jms.ConnectionFactory
import javax.jms.JMSContext

@ApplicationScoped
class MovieProducer(
    val connectionFactory: ConnectionFactory,
    val objectMapper: ObjectMapper
) {

    fun sendMovie(movie: Movie) {
        connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE).use { context ->
            val destination = context.createQueue(MOVIE_SEND_QUEUE_NAME)
            val producer = context.createProducer()
            producer.send(destination, objectMapper.writeValueAsString(movie))
        }
    }

    companion object {
        const val MOVIE_SEND_QUEUE_NAME = "movie-send"
    }

}