package cnt.jdbc.persistence

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "book")
class BookEntity(
    @Id val isbn: String,
    val title: String
) : PanacheEntityBase
