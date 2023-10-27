package cnt.scheduler

import io.mockk.verify
import io.quarkiverse.test.junit.mockk.InjectSpy
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.QuarkusTestProfile
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Test

/**
 * This Integration test is not really useful
 * as this implementation heavily depends on the "out of the box" quarkus scheduler implementation itself.
 * It is also not recommended to test something where you are not able to change its implementation.
 *
 * The reason why this test is here anyway is to show how you could test it if you really need to.
 */
@QuarkusTest
@TestProfile(EnabledAnnotationBasedScheduler::class)
class AnnotationBasedSchedulerIntTest {

    @InjectSpy
    private lateinit var sendBookNewsletter: SendBookNewsletter

    @Test
    fun `enabled scheduler - runs three times according configuration`() {
        Thread.sleep(100)
        verify(atLeast = 1) { sendBookNewsletter.invoke() }
    }
}

class EnabledAnnotationBasedScheduler : QuarkusTestProfile {

    override fun getConfigOverrides(): MutableMap<String, String> {
        return mutableMapOf(
            "scheduler.book-newsletter.interval" to "PT0.030S",
            "scheduler.book-newsletter.enabled" to "true",
            "scheduler.reading-probes-newsletter.enabled" to "false"
        )
    }
}
