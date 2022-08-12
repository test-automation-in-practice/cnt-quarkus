package reactive.port.rest

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.core.Is
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import reactive.domain.book.Book
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEqualTo

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class BookRestControllerIntTest {

    @Test
    @Order(1)
    fun getAllBooks() {
        RestAssured.given()
            .`when`().get("/books")
            .then()
            .statusCode(200)
            .body(Is.`is`("[]"))
    }

    @Test
    @Order(2)
    fun `create and get new book`() {
        val savedPath = RestAssured.given()
            .body(Book(null, "Reactive Quarkus for beginners", "1234567890"))
            .contentType(ContentType.JSON)
            .`when`().post("/books")
            .then()
            .statusCode(201)
            .extract().header("Location")

        RestAssured.given()
            .`when`().get(savedPath)
            .then()
            .statusCode(200)
            .extract().response().jsonPath().apply {
                expectThat(getString("title")).isEqualTo("Reactive Quarkus for beginners")
                expectThat(getLong("id")).isNotEqualTo(0).isNotEqualTo(null)
            }
    }

    @Test
    @Order(3)
    fun `search book by title like`() {
        RestAssured
            .`when`().get("/books/search/Rea")
            .then()
            .statusCode(200)
            .extract().response().jsonPath().apply {
                expectThat(getString("title")).isEqualTo("[Reactive Quarkus for beginners]")
            }
    }

    @Test
    @Order(4)
    fun `delete book`() {
        RestAssured
            .`when`().delete("/books/1")
            .then()
            .statusCode(204)
    }
}
