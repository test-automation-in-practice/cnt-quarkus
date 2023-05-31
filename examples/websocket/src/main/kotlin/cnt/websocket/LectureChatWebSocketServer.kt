package cnt.websocket

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import javax.enterprise.context.ApplicationScoped
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ApplicationScoped
@ServerEndpoint(
    value = "/book-support/{username}",
    encoders = [MessageEnDecoder::class],
    decoders = [MessageEnDecoder::class]
)
class LectureChatWebSocketServer {

    private val logger: Logger = LoggerFactory.getLogger(LectureChatWebSocketServer::class.java)
    private val sessions: MutableMap<String, Session> = ConcurrentHashMap()

    @OnOpen
    fun onOpen(session: Session, @PathParam("username") username: String) {
        val currentNumberOfClients = sessions.keys.count()

        if (sessions.containsKey(username)) {
            throw UserAlreadyPresentException(username)
        }
        sessions[username] = session

        broadcast("User $username joined")
        session.send(username, "You and $currentNumberOfClients users are currently in this session")
        session.send(username, "Welcome to the lecture!")
    }

    @OnClose
    fun onClose(session: Session, closeReason: CloseReason, @PathParam("username") username: String) {
        if (closeReason.closeCode.code == 409) {
            session.send(null, "Session was closed")
        } else {
            broadcast("User $username left the session")
            sessions.remove(username)
        }
    }

    @OnError
    fun onError(session: Session, @PathParam("username") username: String, throwable: Throwable) {
        if (throwable is UserAlreadyPresentException) {
            session.send("System", throwable.localizedMessage)
            session.close(CloseReason({ 409 }, throwable.localizedMessage))
        } else {
            broadcast("User $username received error: ${throwable.message}")
            sessions.remove(username)
        }
    }

    @OnMessage
    fun onMessage(message: Message, @PathParam("username") username: String) {
        broadcast(">> ${message.sentAt} - ${username}: ${message.message}", username)
    }

    private fun broadcast(message: String, username: String? = null) {
        sessions.values.forEach { session ->
            session.asyncRemote.sendObject(Message(message, username)) { result ->
                if (result.exception != null) {
                    logger.error("Unable to send message" + result.exception)
                }
            }
        }
    }

    private fun Session.send(sender: String?, msg: String) = asyncRemote.sendObject(Message(msg, sender))

    data class UserAlreadyPresentException(val username: String) : RuntimeException("User $username has already joined. Take another username")
}
