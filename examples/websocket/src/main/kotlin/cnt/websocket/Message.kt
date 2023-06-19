package cnt.websocket

import java.time.LocalDateTime

@NoArgConstructor
data class Message(
    val message: String,
    val sender: String? = null,
    val sentAt: LocalDateTime = LocalDateTime.now()
)
