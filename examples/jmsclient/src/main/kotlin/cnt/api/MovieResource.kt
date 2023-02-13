package cnt.api

import cnt.adapters.jms.MovieConsumer
import cnt.adapters.jms.MovieProducer
import cnt.adapters.jms.model.Movie
import org.jboss.resteasy.reactive.ResponseStatus
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/movie")
class MovieResource(
    val movieConsumer: MovieConsumer,
    val movieProducer: MovieProducer
) {

    @GET
    fun getMovie(): Movie = movieConsumer.receiveMovie()

    @POST
    @ResponseStatus(201)
    fun addMovie(movie: Movie) = movieProducer.sendMovie(movie)
}