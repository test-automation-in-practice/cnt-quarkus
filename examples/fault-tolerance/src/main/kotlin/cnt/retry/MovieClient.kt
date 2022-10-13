package cnt.retry

import java.io.IOException
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieClient {

    fun getNumberOfMoviesForActor(actor: String): Int? {
        // do something
        return 0
    }
}