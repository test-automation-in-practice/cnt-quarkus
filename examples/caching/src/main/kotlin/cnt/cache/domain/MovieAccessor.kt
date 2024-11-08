package cnt.cache.domain

import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieAccessor(private val movieClient: MovieClient) {

    @CacheResult(cacheName = "movieActor-cache")
    fun getNumberOfMoviesForActor(actor: String): Int? =
        movieClient.getNumberOfMoviesForActor(actor)
}