package reactive.adapter

import reactive.configuration.AddDefaultNoArgConstructor
import javax.persistence.*

@Entity
@Table(
    name = "books",
    uniqueConstraints = [UniqueConstraint(name = "isbnMustBeUnique", columnNames = ["isbn"])]
)
@AddDefaultNoArgConstructor
class BookEntity(

    @Id
    @GeneratedValue
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    var isbn: String,
)
