package cnt.elasticsearch

import cnt.elasticsearch.ElasticSearchConsts.ANALYSE_API
import cnt.elasticsearch.ElasticSearchConsts.INDEX_API
import cnt.elasticsearch.ElasticSearchConsts.INDEX_NAME
import io.quarkus.test.junit.QuarkusTest
import io.vertx.core.json.JsonObject
import org.apache.http.util.EntityUtils
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.client.Request
import org.elasticsearch.client.Response
import org.elasticsearch.client.RestClient
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import jakarta.inject.Inject

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BookRepositoryTest {

    @Inject
    private lateinit var bookRepository: BookRepository
    @Inject
    private lateinit var elasticSearchClient: RestClient
    private val book = Book("Title", "Isbn")

    @Test
    @Order(1)
    fun `create new index called library`() {
        val indexResponse = bookRepository.createIndex(INDEX_NAME).getResponseJson()

        assertThat(indexResponse.getString("index")).isEqualTo(INDEX_NAME)
    }

    /**
     * The Analyze API in Elasticsearch helps to understand how text is processed and indexed.
     * It can be used to see how the input text is transformed by the analysis process,
     * which is crucial for ensuring that the search queries return the most relevant results.
     * This calls the created analyzer with an input text and verifies that it does its job.
     */
    @Test
    @Order(2)
    fun `when text contains umlaut the analyser should not transform it to ae`() {
        val request = Request("POST", ANALYSE_API).apply {
            setJsonEntity(AnalyserRequest("custom_umlauts_transformer_analyzer", "I cänt speak englisch").toJson())
        }

        val caentToken = elasticSearchClient.performRequest(request).getResponseJson()
            .getJsonArray("tokens")
            .map { it as JsonObject }
            .first { it.getInteger("position") == 1 }

        assertThat(caentToken.getString("token")).isEqualTo("cänt")
    }

    @Test
    @Order(3)
    fun `when text contains ae the analyser should transform it to umlaut`() {
        val request = Request("POST", ANALYSE_API).apply {
            setJsonEntity(AnalyserRequest("custom_umlauts_transformer_analyzer", "I caent speak englisch").toJson())
        }

        val elasticSearchResponse = elasticSearchClient.performRequest(request)

        val caentToken = elasticSearchResponse.getResponseJson()
            .getJsonArray("tokens")
            .map { it as JsonObject }
            .first { it.getInteger("position") == 1 }

        assertThat(caentToken.getString("token")).isEqualTo("cänt")

    }

    @Test
    @Order(4)
    fun `insert into the newly created index and find book by its isbn`() {
        // Create Book
        bookRepository.insertBook(book)

        refreshElasticSearchIndex()

        val foundBook = bookRepository.getByIsbn(book.isbn)

        assertThat(foundBook).isEqualTo(book)
    }

    @Test
    @Order(5)
    fun `find all documents in index`() {
        val books = bookRepository.findAllBooks()

        assertThat(books.totalHits).isEqualTo(1)
        assertThat(books.results).first().extracting { it.title }.isEqualTo(book.title)
    }

    @Test
    @Order(6)
    fun `search with ae returns a book with title containing the umlaut`() {
        // Create Book
        val book = Book("Gehen auf türkisch heißt yürüyüsü", "987123654123")
        bookRepository.insertBook(book)

        refreshElasticSearchIndex()

        // Assert that book was created
        val bookSearchResult = bookRepository.searchBooksByTitle("yürüyüsü")

        assertThat(bookSearchResult.totalHits).isEqualTo(1)
        assertThat(bookSearchResult.results).first().isEqualTo(book)
    }

    @Test
    @Order(7)
    fun `execute more complex search query to get all book where its title starts with abc`() {
        // insert 1000 books
        (1..5).map { Book("abc$it", "$it") }.map { bookRepository.insertBook(it) }
        (6..10).map { Book("${it}abc", "$it") }.map { bookRepository.insertBook(it) }
        refreshElasticSearchIndex()

        val bookSearchResult = bookRepository.searchBooksStartingWith("abc")

        assertThat(bookSearchResult.results).hasSize(5).allMatch { it.title.startsWith("abc") }
    }

    private fun Response.getResponseJson(): JsonObject {
        return JsonObject(EntityUtils.toString(entity))
    }

    data class AnalyserRequest(val analyzer: String, val text: String) {
        fun toJson(): String = JsonObject.mapFrom(this).toString()
    }

    /**
     * An Update on the elastic database does not mean the index itself is refreshed.
     * The refresh of the index happens asynchronously.
     * To write tests without blocking the thread and hope that after a certain amount of time
     * the refresh of the index is done we can execute that manually by calling the refresh api of our books index.
     */
    private fun refreshElasticSearchIndex() {
        val request = Request("GET", "$INDEX_API/_refresh")
        elasticSearchClient.performRequest(request)
    }
}
