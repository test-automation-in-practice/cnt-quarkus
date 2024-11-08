package cnt.retry

import java.io.IOException
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieClient {

    fun getNumberOfMoviesForActor(actor: String): Int? {
        // do something
        return 0
    }
}