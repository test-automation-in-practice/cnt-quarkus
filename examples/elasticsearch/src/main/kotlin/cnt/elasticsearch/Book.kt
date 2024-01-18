package cnt.elasticsearch

data class Book(
    val title: String,
    val isbn: String
)

data class BookSearchResult(val timeTook: Int, val totalHits: Int, val results: List<Book>)
