package cnt.adapters.restclient

import cnt.adapters.restclient.model.Movie
import cnt.adapters.restclient.model.Movies
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON


@RegisterRestClient
@RegisterProvider(MovieRestClientExceptionMapper::class)
interface MovieRestClient {

    @GET
    @Path("/movies")
    fun getAllMovies(): Movies

    @GET
    @Path("/movie/{id}")
    fun getMovieById(@PathParam("id") id: String): Movie

    @POST
    @Produces(APPLICATION_JSON)
    @Path("/movie")
    fun addMovie(movie: Movie): Movie
}