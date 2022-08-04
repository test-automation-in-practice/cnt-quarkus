package cnt.jpa.domain

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Movie(
    @Id
    val id: Long,
    val title: String,
    val publishYear: Int
)
