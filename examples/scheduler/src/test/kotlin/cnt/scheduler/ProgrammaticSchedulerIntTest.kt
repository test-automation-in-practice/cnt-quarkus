package cnt.scheduler

import io.mockk.verify
import io.quarkiverse.test.junit.mockk.InjectSpy
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Test

/**
 * As a developer can configure the programmatically defined scheduler incorrectly this integration makes partially sense.
 * This programmatically defined scheduler uses the Quartz library to schedule a job.
 * As a developer I don't want to test this official library.
 * Integration tests come in with a cost. And the whole quartz implementation can be tested with unit tests like [ProgrammaticSchedulerTest].
 */
@QuarkusTest
@TestProfile(EnabledProgrammaticBasedScheduler::class)
class ProgrammaticSchedulerIntTest {

    @InjectSpy
    private lateinit var job: SendReadingProbesNewsletterJob

    @Test
    fun `enabled scheduler - runs configured time`() {
        Thread.sleep(100)
        verify(atLeast = 1) { job.execute(any()) }
    }
}

class EnabledProgrammaticBasedScheduler : QuarkusTestProfile {

    override fun getConfigOverrides(): MutableMap<String, String> {
        return mutableMapOf(
            "scheduler.book-newsletter.enabled" to "false",
            "scheduler.reading-probes-newsletter.enabled" to "true",
            "scheduler.reading-probes-newsletter.interval-in-milliseconds" to "30"
        )
    }
}
