package cnt.adapters.restclient.model

data class Movie(
    var id: String? = null,
    var title: String? = null,
    var publishYear: Int? = null,
    var cast: List<Actor>? = emptyList()
)
