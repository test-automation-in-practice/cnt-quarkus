package cnt.mongodb.domain

import io.quarkus.mongodb.panache.PanacheMongoRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieRepository : PanacheMongoRepository<Movie> {

    fun findByTitle(title: String): Movie? {
        return find("title like ?1", title).firstResult()
    }

    fun findByActorName(name: String): List<Movie> {
        return list("cast.name like ?1", name)
    }

}