package cnt.scheduler

import cnt.scheduler.ProgrammaticScheduler.Companion.SEND_READING_PROBES_IDENTITY_SCHEDULER
import cnt.scheduler.ProgrammaticScheduler.Companion.SEND_READING_PROBES_IDENTITY_TRIGGER
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.quarkus.runtime.StartupEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.quartz.Scheduler
import org.quartz.impl.JobDetailImpl
import org.quartz.impl.triggers.SimpleTriggerImpl
import java.util.Date

/**
 * This is the preferred way to test the programmatically defined scheduler as we don't need to have a running quarkus context
 * which needs time and resources.
 */
class ProgrammaticSchedulerTest {

    @Nested
    inner class AddSendBookNewsletterScheduler {

        private val quartz: Scheduler = mockk()
        private val sendReadingProbesNewsletterConfig: SendReadingProbesNewsletterConfig = mockk()
        private val cut: ProgrammaticScheduler = ProgrammaticScheduler(quartz, sendReadingProbesNewsletterConfig)

        private val event: StartupEvent = mockk()

        @Test
        fun `scheduler is disabled - should not run`() {
            every { sendReadingProbesNewsletterConfig.enabled() } returns false

            cut.addSendBookNewsletterScheduler(event)

            verify { quartz wasNot called }
        }

        @Test
        fun `scheduler is enabled - should schedule job`() {
            with(sendReadingProbesNewsletterConfig) {
                every { enabled() } returns true
                every { intervalInMilliseconds() } returns 30
            }
            every { quartz.scheduleJob(any(), any()) } returns Date()

            cut.addSendBookNewsletterScheduler(event)

            verify {
                quartz.scheduleJob(
                    withArg<JobDetailImpl> { jobDetail ->
                        assertThat(jobDetail.name).isEqualTo(SEND_READING_PROBES_IDENTITY_SCHEDULER)
                    },
                    withArg<SimpleTriggerImpl> { trigger ->
                        assertThat(trigger.repeatCount).isEqualTo(-1) // -1 means repeat forever
                        assertThat(trigger.name).isEqualTo(SEND_READING_PROBES_IDENTITY_TRIGGER)
                    }
                )
            }
        }
    }
}
