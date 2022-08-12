package reactive.adapter

import io.mockk.every
import io.mockk.mockk
import io.smallrye.mutiny.Uni
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactive.subscribeAssert
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

@Suppress("ReactiveStreamsUnusedPublisher")
internal class BookReactiveHttpClientExternalServiceTest {

    private val httpClient: HttpClient = mockk()
    private val cut = BookReactiveHttpClientExternalService(httpClient)

    @Nested
    inner class FindBookByISBN {

        @Test
        fun `get book - return book`() {
            val happyResponse = BooksResponse(
                items = listOf(
                    BookResponse(
                        volumeInfo = VolumeInfo("Quarkus")
                    )
                )
            )
            every { httpClient.findBookByIsbn(any()) } returns Uni.createFrom().item(happyResponse)

            val response = cut.findBookByISBN("123456789")
                .subscribeAssert()
                .assertCompleted()
                .item

            expectThat(response) {
                get { id }.isNull()
                get { isbn }.isEqualTo("123456789")
                get { title }.isEqualTo("Quarkus")
            }
        }

        @Test
        fun `book does not exist - return null`() {
            every { httpClient.findBookByIsbn(any()) } returns Uni.createFrom().item(BooksResponse(null))

            cut.findBookByISBN("123456789")
                .subscribeAssert()
                .assertCompleted()
                .assertItem(null)
        }
    }
}
