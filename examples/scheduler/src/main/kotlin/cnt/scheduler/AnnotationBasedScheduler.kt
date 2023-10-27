package cnt.scheduler

import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AnnotationBasedScheduler(
    private val sendBookNewsletter: SendBookNewsletter,
    @ConfigProperty(name = "scheduler.book-newsletter.enabled") private val sendBookNewsletterEnabled: Boolean
) {

    @Scheduled(identity = "NewsletterScheduler", every = "{scheduler.book-newsletter.interval}")
    fun newsletterScheduler() {
        if (sendBookNewsletterEnabled) {
            sendBookNewsletter()
        }
    }
}

@ApplicationScoped
class SendBookNewsletter {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SendBookNewsletter::class.java)
    }

    /** Simulates a function which would send a book newsletter to all subscribed users */
    operator fun invoke() {
        LOGGER.info("Sent book newsletter")
    }
}
