package cnt.cache.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import java.io.IOException
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


private const val MARC_HAMILL = "Marc Hamill"
private const val FELICITY_JONES = "Felicity Jones"
private const val CARRIE_FISCHER = "Carrie Fischer"

/**
 * The correct functionality of our own code is verified using unit-tests.
 */
internal class MovieAccessorTest {

    private val client: MovieClient = mockk()
    private val cut = MovieAccessor(client)

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

}

/**
 * Since caching is a framework feature, it cannot be unit-tested.
 *
 * In this technology integration test the real [io.quarkus.cache.CacheResult] is used
 * to verify that the [MovieAccessor] actually behaves like it should
 * in regard to caching.
 *
 * In this example:
 * - tests would fail if a non-existing cache was referenced
 * - tests would fail if the desired _unless_ condition was wrong
 * - tests would fail if caching would not be enabled by the [io.quarkus.cache.CacheResult]
 */
@QuarkusTest
internal class MovieAccessorCacheTest {

    @InjectMock
    lateinit var client: MovieClient

    @Inject
    lateinit var movieAccessor: MovieAccessor

    @Test
    fun `caches results if there are any`() {
        every { client.getNumberOfMoviesForActor(MARC_HAMILL) } returns 46
        every { client.getNumberOfMoviesForActor(FELICITY_JONES) } returns 38

        repeat(10) {
            assertThat(movieAccessor.getNumberOfMoviesForActor(MARC_HAMILL)).isEqualTo(46)
            assertThat(movieAccessor.getNumberOfMoviesForActor(FELICITY_JONES)).isEqualTo(38)
        }

        verify(exactly = 1) { client.getNumberOfMoviesForActor(MARC_HAMILL) }
        verify(exactly = 1) { client.getNumberOfMoviesForActor(FELICITY_JONES) }
    }

    @Test
    fun `does not cache if there was an Exception`() {
        every { client.getNumberOfMoviesForActor(CARRIE_FISCHER) } throws IOException("Foo")

        repeat(10) {
            assertThrows<IOException> {
                movieAccessor.getNumberOfMoviesForActor(CARRIE_FISCHER)
            }
        }
        verify(exactly = 10) { client.getNumberOfMoviesForActor(CARRIE_FISCHER) }
    }
}