package cnt.scheduler

import io.quarkus.runtime.StartupEvent
import io.smallrye.config.ConfigMapping
import org.quartz.Job
import org.quartz.JobBuilder.newJob
import org.quartz.JobExecutionContext
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

/**
 * This [ProgrammaticScheduler] will add a scheduler programmatically via the quartz library
 */
@ApplicationScoped
class ProgrammaticScheduler(
    private val quartz: Scheduler,
    private val sendReadingProbesNewsletterConfig: SendReadingProbesNewsletterConfig,
) {

    fun addSendBookNewsletterScheduler(@Observes event: StartupEvent) {
        if (!sendReadingProbesNewsletterConfig.enabled()) {
            return
        }

        val job = newJob(SendReadingProbesNewsletterJob::class.java)
            .withIdentity(SEND_READING_PROBES_IDENTITY_SCHEDULER)
            .build()
        val trigger = newTrigger()
            .withIdentity(SEND_READING_PROBES_IDENTITY_TRIGGER)
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInMilliseconds(sendReadingProbesNewsletterConfig.intervalInMilliseconds())
                    .repeatForever()
            )
            .build()

        quartz.scheduleJob(job, trigger)
    }

    companion object {
        const val SEND_READING_PROBES_IDENTITY_SCHEDULER = "SendReadingProbesNewsletter"
        const val SEND_READING_PROBES_IDENTITY_TRIGGER = "SendReadingProbesTrigger"
    }
}

@ApplicationScoped
class SendReadingProbesNewsletterJob : Job {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(SendReadingProbesNewsletterJob::class.java)
    }

    /** Simulates a function which would send reading probes newsletter to all subscribed users */
    override fun execute(context: JobExecutionContext) {
        LOGGER.info("Sent reading probes newsletter")
    }
}

@ConfigMapping(prefix = "scheduler.reading-probes-newsletter")
interface SendReadingProbesNewsletterConfig {
    fun enabled(): Boolean
    fun intervalInMilliseconds(): Long
}
