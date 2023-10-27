package cnt.scheduler

import io.mockk.called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AnnotationBasedSchedulerTest {

    @Nested
    inner class NewsletterScheduler {

        private val sendBookNewsletter: SendBookNewsletter = mockk()

        @Test
        fun `scheduler is disabled - should not run`() {
            val cut = AnnotationBasedScheduler(sendBookNewsletter, false)

            cut.newsletterScheduler()

            verify { sendBookNewsletter wasNot called }
        }

        @Test
        fun `scheduler is enabled - will send newsletter`() {
            val cut = AnnotationBasedScheduler(sendBookNewsletter, true)
            every { sendBookNewsletter.invoke() } just runs

            cut.newsletterScheduler()

            verify { sendBookNewsletter.invoke() }
        }
    }
}
