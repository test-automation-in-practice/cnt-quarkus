package cnt.graphql.business

import java.util.*

@AddDefaultNoArgConstructor
data class Movie(
    var id: UUID? = null,
    var title: String,
    var director: String,
    var publishYear: Int
)
