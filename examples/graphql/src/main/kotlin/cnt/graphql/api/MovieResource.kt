package cnt.graphql.api

import cnt.graphql.business.Movie
import cnt.graphql.business.MovieService
import org.eclipse.microprofile.graphql.Description
import org.eclipse.microprofile.graphql.GraphQLApi
import org.eclipse.microprofile.graphql.Mutation
import org.eclipse.microprofile.graphql.Query
import java.util.*


@GraphQLApi
class MovieResource(
    private val movieService: MovieService
) {

    @Query
    @Description("Get all movies")
    fun getAllMovies() = movieService.getAll()

    @Query
    @Description("Get a specific movie by its ID")
    fun getMovie(id: UUID) = movieService.getSingle(id)

    @Mutation
    @Description("Create and save a new movie")
    fun createMovie(movie: Movie) = movieService.add(movie)

    @Mutation
    @Description("Delete a movie by its ID")
    fun deleteMovie(id: UUID) = movieService.delete(id)

//    @Query("allActors")
//    @Description("Get all Actors")
//    fun getAllActors() = movieService.getAllActors()
}