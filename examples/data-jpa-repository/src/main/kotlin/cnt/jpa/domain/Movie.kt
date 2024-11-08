package cnt.jpa.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Movie(
    @Id
    val id: Long,
    val title: String,
    val publishYear: Int
)
