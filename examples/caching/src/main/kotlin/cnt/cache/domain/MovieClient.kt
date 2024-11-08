package cnt.cache.domain

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieClient {

    fun getNumberOfMoviesForActor(actor: String): Int? {
        // do something
        return 0
    }
}