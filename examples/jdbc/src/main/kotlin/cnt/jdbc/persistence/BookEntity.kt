package cnt.jdbc.persistence

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "book")
class BookEntity(
    @Id val isbn: String,
    val title: String
) : PanacheEntityBase
