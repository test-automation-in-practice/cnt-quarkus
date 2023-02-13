package cnt

import cnt.adapters.jms.MovieConsumer
import cnt.adapters.jms.MovieProducer
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions
import org.hamcrest.core.Is
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.jms.ConnectionFactory
import javax.jms.JMSContext

@QuarkusTest
class MovieEndToEndTest {

    @Inject
    lateinit var connectionFactory: ConnectionFactory

    @Nested
    inner class GetMovie {

        @Test
        fun `should recieve a movie from queue, when getMovie endpoint is called`(){

            val movieToReceive =
                """{"id":"1","title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[{"id":"1","name":"Marc Hamill","inAs":"Luke Skywalker"},{"id":"2","name":"Carrie Fisher","inAs":"Leia Organa"}]}"""
            connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE).use { context ->
                val destination = context.createQueue(MovieConsumer.MOVIE_RECIEVE_QUEUE_NAME)
                val producer = context.createProducer()
                producer.send(destination, movieToReceive)
            }

            RestAssured.given()
                .`when`().get("/movie")
                .then()
                .statusCode(200)
                .body(Is.`is`(movieToReceive))
        }
    }

    @Nested
    inner class AddMovie {

        @Test
        fun `should add a movie to queue when postMovie endpoint is called`() {
            val movieToSend = """{"id":"1","title":"Star Wars Episode IV - A New Hope","publishYear":1977,"cast":[{"id":"1","name":"Marc Hamill","inAs":"Luke Skywalker"},{"id":"2","name":"Carrie Fisher","inAs":"Leia Organa"}]}"""

            RestAssured.given()
                .body(movieToSend)
                .contentType(ContentType.JSON)
                .`when`().post("/movie").then()
                .statusCode(201)

            val actualMovie =  connectionFactory.createContext().use { context ->
                val destination = context.createQueue(MovieProducer.MOVIE_SEND_QUEUE_NAME)
                val consumer = context.createConsumer(destination)
                consumer.receiveBody(String::class.java, 2000L)
            }
            Assertions.assertThat(actualMovie).isEqualTo(movieToSend)

        }
    }

}