package cnt.adapters.jms

import cnt.adapters.jms.model.Movie
import com.fasterxml.jackson.databind.ObjectMapper
import javax.enterprise.context.ApplicationScoped
import javax.jms.ConnectionFactory
import javax.jms.JMSContext

@ApplicationScoped
class MovieConsumer(
    val connectionFactory: ConnectionFactory,
    val objectMapper: ObjectMapper
) {

    fun receiveMovie() : Movie {
        connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE).use { context ->
            val destination = context.createQueue(MOVIE_RECIEVE_QUEUE_NAME)
            val consumer = context.createConsumer(destination)
            return objectMapper.readValue(consumer.receiveBody(String::class.java, 2000L), Movie::class.java)
        }
    }

    companion object {
        const val MOVIE_RECIEVE_QUEUE_NAME = "movie-receive"
    }
}