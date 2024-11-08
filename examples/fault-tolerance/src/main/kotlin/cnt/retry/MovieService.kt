package cnt.retry

import jakarta.enterprise.context.ApplicationScoped
import javax.net.ssl.SSLException
import org.eclipse.microprofile.faulttolerance.Retry

@ApplicationScoped
class MovieService(private val movieClient: MovieClient) {

    @Retry(abortOn=[SSLException::class])
    fun getNumberOfMoviesForActor(actor: String): Int? =
        movieClient.getNumberOfMoviesForActor(actor)
}