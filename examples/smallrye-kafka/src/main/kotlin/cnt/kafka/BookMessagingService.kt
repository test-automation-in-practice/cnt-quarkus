package cnt.kafka

import cnt.kafka.domain.BookUpdatedEvent
import cnt.kafka.domain.EditBookTitleEvent
import org.eclipse.microprofile.reactive.messaging.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletionStage
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BookMessagingService(
    @Channel(BOOK_UPDATED_EVENT) private val bookUpdatedEventEmitter: Emitter<BookUpdatedEvent>
) {

    companion object {
        const val EDIT_BOOK_TITLE_EVENT = "edit-book-title-event"
        const val BOOK_UPDATED_EVENT = "book-updated-event"
    }

    private val logger: Logger = LoggerFactory.getLogger(BookMessagingService::class.java)

    @Incoming(EDIT_BOOK_TITLE_EVENT)
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    fun receiveBookTitle(message: Message<EditBookTitleEvent>): CompletionStage<Void> {
        logger.info("Received BookTitle message with title [${message.payload.title}]")

        if (isBookTitleValid(message.payload.title)) {
            logger.info("Event contains an error")
            return message.nack(Throwable("Something went wrong"))
        }

        logger.info("Sending new BookEvent")
        return message.ack().thenAccept { bookUpdatedEventEmitter.send(BookUpdatedEvent(message.payload.title)) }
    }

    fun isBookTitleValid(bookTitle: String) = !bookTitle[0].isUpperCase()
}
