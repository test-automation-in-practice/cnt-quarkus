package cnt.kafka

import cnt.kafka.domain.BookUpdatedEvent
import cnt.kafka.domain.EditBookTitleEvent
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector
import io.smallrye.reactive.messaging.providers.connectors.InMemorySink
import io.smallrye.reactive.messaging.providers.connectors.InMemorySource
import org.assertj.core.api.Assertions
import org.awaitility.Awaitility
import org.eclipse.microprofile.reactive.messaging.spi.Connector
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Duration
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource(BookMessagingServiceTest.KafkaTestResourceLifecycleManager::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookMessagingServiceTest {

    @Inject
    @Connector("smallrye-in-memory")
    private lateinit var inMemoryConnector: InMemoryConnector

    private lateinit var editBookTitleTopic: InMemorySource<EditBookTitleEvent>
    private lateinit var bookUpdatedTopic: InMemorySink<BookUpdatedEvent>

    @BeforeAll
    fun setup() {
        editBookTitleTopic = inMemoryConnector.source(BookMessagingService.EDIT_BOOK_TITLE_EVENT)
        bookUpdatedTopic = inMemoryConnector.sink(BookMessagingService.BOOK_UPDATED_EVENT)
    }

    @BeforeEach
    fun reset() {
        bookUpdatedTopic.clear()
    }

    @Test
    fun `receive event and send book successfully`() {
        val editBookTitleEvent = EditBookTitleEvent("How to Test Quarkus Kafka")
        editBookTitleTopic.send(editBookTitleEvent)

        Awaitility.await().until { bookUpdatedTopic.received().size == 1 }

        Assertions.assertThat(bookUpdatedTopic.received())
            .hasSize(1)
            .first()
            .returns("How to Test Quarkus Kafka") { it.payload.title }
    }

    @Test
    fun `receive event where title is not upper case which leads into an exception`() {
        val editBookTitleEvent = EditBookTitleEvent("how to Test Quarkus Kafka")
        editBookTitleTopic.send(editBookTitleEvent)

        Awaitility.await().atMost(Duration.ofSeconds(1))

        Assertions.assertThat(bookUpdatedTopic.received()).isEmpty()
    }

    class KafkaTestResourceLifecycleManager : QuarkusTestResourceLifecycleManager {

        override fun start(): MutableMap<String, String> {
            return mutableMapOf<String, String>().apply {
                putAll(InMemoryConnector.switchIncomingChannelsToInMemory(BookMessagingService.EDIT_BOOK_TITLE_EVENT))
                putAll(InMemoryConnector.switchOutgoingChannelsToInMemory(BookMessagingService.BOOK_UPDATED_EVENT))
            }
        }

        override fun stop() {
            InMemoryConnector.clear()
        }
    }
}
