package cnt.retry

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import java.io.IOException
import java.net.ConnectException
import jakarta.inject.Inject
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private const val MARC_HAMILL = "Marc Hamill"
private const val FELICITY_JONES = "Felicity Jones"

/**
 * The correct functionality of our own code is verified using unit-tests.
 */
internal class MovieServiceTest {

    private val client: MovieClient = mockk()
    private val cut = MovieService(client)

    @Test
    fun `invokes client and returns number of pages if found`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } returns 46
        val actual = cut.getNumberOfMoviesForActor(MARC_HAMILL)
        assertThat(actual).isEqualTo(46)
    }

    @Test
    fun `invokes invokes client and returns null if null was returned`() {
        every { client.getNumberOfMoviesForActor(FELICITY_JONES) } returns null
        val actual = cut.getNumberOfMoviesForActor(FELICITY_JONES)
        assertThat(actual).isNull()
    }

    @Test
    fun `invokes invokes client and rethrow IOExeption`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } throws IOException("Foo")
        assertThrows<IOException> {
            cut.getNumberOfMoviesForActor(MARC_HAMILL)
        }
    }
}

/**
 * Since retry functionality is a framework feature, it cannot be unit-tested.
 *
 * In this technology integration test the real [org.eclipse.microprofile.faulttolerance.Retry] is used
 * to verify that the [MovieService] actually behaves like it should
 * in regard to retries.
 */
@QuarkusTest
internal class MovieServiceRetryTest {

    @InjectMock
    lateinit var client: MovieClient

    @Inject
    lateinit var movieService: MovieService

    @Test
    fun `should execute getNumberOfMoviesForActor only once when no exception occurs`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } returns 46

        assertThat(movieService.getNumberOfMoviesForActor(MARC_HAMILL)).isEqualTo(46)

        verify(exactly = 1) { client.getNumberOfMoviesForActor(MARC_HAMILL) }
    }

    @Test
    fun `should execute getNumberOfMoviesForActor two times when first retry was successful`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } throws ConnectException("Foo") andThen 46

        assertThat(movieService.getNumberOfMoviesForActor(MARC_HAMILL)).isEqualTo(46)

        verify(exactly = 2) { client.getNumberOfMoviesForActor(MARC_HAMILL) }
    }

    @Test
    fun `should execute getNumberOfMoviesForActor three times when second retry was successful`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } throws ConnectException("Foo") andThenThrows ConnectException("Foo") andThen 46

        assertThat(movieService.getNumberOfMoviesForActor(MARC_HAMILL)).isEqualTo(46)

        verify(exactly = 3) { client.getNumberOfMoviesForActor(MARC_HAMILL) }
    }

    @Test
    fun `should throw the exception when second retry was not successful`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } throws ConnectException("Foo")

        assertThrows<ConnectException> {
            assertThat(movieService.getNumberOfMoviesForActor(MARC_HAMILL)).isEqualTo(46)
        }

        verify(exactly = 3) { client.getNumberOfMoviesForActor(MARC_HAMILL) }
    }

    @Test
    fun `should throw SSLException directly without retry, because this kind of exception should not be retried`(){

        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } throws SSLHandshakeException("Foo")

        assertThrows<SSLException> {
            assertThat(movieService.getNumberOfMoviesForActor(MARC_HAMILL)).isEqualTo(46)
        }

        verify(exactly = 1) { client.getNumberOfMoviesForActor(MARC_HAMILL) }

    }
}
