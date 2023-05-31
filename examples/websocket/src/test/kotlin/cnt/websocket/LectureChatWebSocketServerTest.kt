package cnt.websocket

import io.quarkus.test.common.http.TestHTTPResource
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.net.URI
import java.util.concurrent.LinkedBlockingDeque
import javax.websocket.ClientEndpoint
import javax.websocket.ContainerProvider
import javax.websocket.OnMessage
import javax.websocket.Session

@QuarkusTest
class LectureChatWebSocketServerTest {

    @TestHTTPResource("/book-support/Client1")
    private lateinit var client1: URI

    @TestHTTPResource("/book-support/Client2")
    private lateinit var client2: URI

    @BeforeEach
    fun setup() {
        LectureChatTestClient.clearMessages()
    }

    @Test
    fun `connect to websocket`() {
        connectToServer(client1).use {
            val recordedMessage = LectureChatTestClient.returnExpectedMessages(3)

            assertThat(recordedMessage)
                .hasSize(4)
                .filteredOn { it != null }
                .hasSize(3) // one null value removed to verify no more messages besides following
                .extracting("message")
                .containsExactly(
                    "User Client1 joined",
                    "You and 0 users are currently in this session",
                    "Welcome to the lecture!",
                )
        }
    }

    @Test
    fun `connect to websocket and send message`() {
        connectToServer(client1).use { session ->
            session.asyncRemote.sendObject(Message("Hello I like your book!"))

            val recordedMessage = LectureChatTestClient.returnExpectedMessages(4)

            assertThat(recordedMessage)
                .hasSize(5)
                .filteredOn { it != null }
                .hasSize(4) // one null value removed to verify no more messages besides following
                .extracting("message")
                .map<String> { (it as String).substringAfterLast("-").removePrefix(" ") }
                .containsExactly(
                    "User Client1 joined",
                    "You and 0 users are currently in this session",
                    "Welcome to the lecture!",
                    "Client1: Hello I like your book!"
                )
        }
    }

    @Test
    fun `two user with same username try to start a session`() {
        val session1 = connectToServer(client1)
        val session2 = connectToServer(client1)
        val recordedMessage = LectureChatTestClient.returnExpectedMessages(5)

        session1.close()
        session2.close()

        assertThat(recordedMessage)
            .hasSize(6)
            .filteredOn { it != null }
            .hasSize(5) // one null value removed to verify no more messages besides following
            .extracting("message")
            .map<String> { (it as String).substringAfterLast("-").removePrefix(" ") }
            .containsExactlyInAnyOrder(
                "User Client1 joined",
                "User Client1 has already joined. Take another username",
                "Session was closed",
                "You and 0 users are currently in this session",
                "Welcome to the lecture!",
            )
    }

    @Test
    fun `two different user starting a session - client1 sends a message - and after that one leaves`() {
        val sessionClient1 = connectToServer(client1)
        val sessionClient2 = connectToServer(client2)
        sessionClient2.close()
        val recordedMessage = LectureChatTestClient.returnExpectedMessages(5)

        sessionClient1.close()
        assertThat(recordedMessage)
            .hasSize(6)
            .filteredOn { it != null }
            .hasSize(5) // one null value removed to verify no more messages besides following
            .extracting("message")
            .map<String> { (it as String).substringAfterLast("-").removePrefix(" ") }
            .containsExactlyInAnyOrder(
                "User Client1 joined",
                "You and 0 users are currently in this session",
                "Welcome to the lecture!",
                "User Client2 joined",
                "User Client2 left the session",
            )
    }

    private fun connectToServer(uri: URI): Session {
        return ContainerProvider.getWebSocketContainer().connectToServer(LectureChatTestClient::class.java, uri)
    }

    @ClientEndpoint(
        encoders = [MessageEnDecoder::class],
        decoders = [MessageEnDecoder::class]
    )
    class LectureChatTestClient {

        @OnMessage
        fun onMessage(msg: Message) {
            message.add(msg)
        }

        companion object {
            private val message = LinkedBlockingDeque<Message>()

            fun clearMessages() {
                message.clear()
            }

            fun returnExpectedMessages(expectedElements: Int): List<Message?> {
                Awaitility.await().until { message.size == expectedElements }
                return (0..expectedElements).map { message.poll() }
            }
        }
    }
}
