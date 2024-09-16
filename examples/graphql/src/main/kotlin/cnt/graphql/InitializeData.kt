package cnt.graphql

import cnt.graphql.business.Movie
import cnt.graphql.business.MovieService
import io.quarkus.runtime.Startup
import javax.enterprise.context.ApplicationScoped

@Startup
@ApplicationScoped
class InitializeData {
    private val movieService: MovieService

    constructor(movieService: MovieService) {
        this.movieService = movieService
        // initializeMovies() // uncomment this to create a list of movies on app startup
    }

    private fun initializeMovies() =
        listOf(
            Movie(
                title = "Pulp Fiction",
                director = "Quentin Tarantino",
                publishYear = 1994
            ),
            Movie(
                title = "Jackie Brown",
                director = "Quentin Tarantino",
                publishYear = 1997
            )
        ).forEach {
            movieService.add(it)
        }
}