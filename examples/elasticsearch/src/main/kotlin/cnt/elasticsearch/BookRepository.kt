package cnt.elasticsearch

import cnt.elasticsearch.ElasticSearchConsts.SEARCH_API
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.apache.http.util.EntityUtils
import org.elasticsearch.client.Request
import org.elasticsearch.client.Response
import org.elasticsearch.client.RestClient
import javax.enterprise.context.ApplicationScoped


/**
 * This Service provides access to the elastic search cluster
 */
@ApplicationScoped
class BookRepository(
    private val elasticSearchClient: RestClient,
) {

    fun createIndex(indexName: String): Response {
        return elasticSearchClient.performRequest(Request("PUT", ElasticSearchConsts.INDEX_NAME).apply {
            setJsonEntity(
                ElasticSearchIndex(
                    settings = Settings(
                        numberOfShards = 1,
                        totalMappingFieldsLimit = 10,
                        analysis = Analysis(
                            charFilter = mapOf(
                                "replace_umlauts" to CharFilter(
                                    type = "mapping",
                                    mappings = listOf("ae=>ä", "ue=>ü", "oe=>ö")
                                )
                            ),
                            filter = mapOf(
                                "to_lowercase" to Filter(type = "lowercase")
                            ),
                            analyzer = mapOf(
                                "custom_umlauts_transformer_analyzer" to Analyzer(
                                    type = "custom",
                                    charFilter = listOf("replace_umlauts"),
                                    tokenizer = "standard",
                                    filter = listOf("to_lowercase")
                                )
                            )
                        )
                    ),
                    mappings = Mappings(
                        properties = mapOf(
                            "title" to Property(
                                type = "text",
                                analyzer = "custom_umlauts_transformer_analyzer"
                            )
                        )
                    )
                ).toJson()
            )
        })
    }

    fun searchBooksStartingWith(prefix: String): BookSearchResult {
        val query = JsonObject(mapOf(
            "bool" to JsonObject(mapOf(
                "must" to JsonArray(listOf(
                    JsonObject(mapOf(
                        "prefix" to JsonObject(mapOf(
                            "title" to prefix
                        ))
                    ))
                )),
            ))
        ))

        return search(query)
    }

    fun searchBooksByTitle(title: String): BookSearchResult {
        val query = JsonObject(
            mapOf(
                "match" to JsonObject(
                    mapOf(
                        "title" to title,
                    )
                )
            )
        )

        return search(query)
    }

    private fun search(searchQueryRequest: JsonObject): BookSearchResult {
        return elasticSearchClient.performRequest(Request("GET", SEARCH_API)
            .apply { setJsonEntity(SearchQueryRequest(searchQueryRequest).toJson()) })
            .toBookSearchResult()
    }

    /**
     * This creates a new [book] in the elastic search database
     */
    fun insertBook(book: Book): Response {
        val request = Request("POST", ElasticSearchConsts.CREATE_API + book.isbn)
        request.setJsonEntity(JsonObject.mapFrom(book).toString())

        return elasticSearchClient.performRequest(request)
    }

    /**
     * This returns the found book by its [isbn]
     */
    fun getByIsbn(isbn: String): Book {
        val request = Request("GET", ElasticSearchConsts.DOC_API + isbn)
        val response = elasticSearchClient.performRequest(request)
        return JsonObject(EntityUtils.toString(response.entity)).toBook()
    }

    /**
     * Returns all books in the elastic search database
     */
    fun findAllBooks(): BookSearchResult {
        val request = Request("GET", SEARCH_API).apply {
            setJsonEntity(
                """
                    {
                        "query": {
                            "match_all": {}
                        }
                    }
                """.trimIndent()
            )
        }
        val response = elasticSearchClient.performRequest(request)

        return response.toBookSearchResult()
    }

    private fun Response.toBookSearchResult(): BookSearchResult {
        val responseBody = EntityUtils.toString(entity)

        val responseJson = JsonObject(responseBody)
        val hitsSummary = responseJson.getJsonObject("hits")
        val books = hitsSummary.getJsonArray("hits")

        val results = (0 until books.size()).map { books.getJsonObject(it).toBook() }
        val timeTook = responseJson.getInteger("took")
        val totalHits = hitsSummary.getJsonObject("total").getInteger("value")

        return BookSearchResult(timeTook, totalHits, results)
    }

    private fun Any.toJson(): String = JsonObject.mapFrom(this).toString()

    private fun JsonObject.toBook(): Book = getJsonObject("_source").mapTo(Book::class.java)
}
