package cnt.jpa.domain

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MovieRepository : PanacheRepository<Movie> {

    fun findByTitle(title: String): List<Movie> {
        return find("title", title).list()
    }
}