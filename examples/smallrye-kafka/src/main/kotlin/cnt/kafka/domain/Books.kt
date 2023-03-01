package cnt.kafka.domain

import cnt.kafka.configuration.AddDefaultNoArgConstructor
import java.time.Instant
import java.util.*

@AddDefaultNoArgConstructor
data class EditBookTitleEvent(val title: String)

data class BookUpdatedEvent(
    val title: String,
    val uuid: String = UUID.randomUUID().toString(),
    val timestamp: Instant = Instant.now()
)
