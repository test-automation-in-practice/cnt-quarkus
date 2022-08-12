package reactive.port.rest

import org.hibernate.HibernateException
import org.jboss.resteasy.reactive.RestResponse
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import javax.persistence.PersistenceException
import javax.ws.rs.NotFoundException

internal class RestExceptionHandlerTest {

    private val cut = RestExceptionHandler()

    @Test
    fun mapDuplicateItem() {
        val exception = HibernateException("Some duplicate ids")

        val response = cut.mapDuplicateItem(exception)

        expectThat(response) {
            get { status }.isEqualTo(RestResponse.Status.BAD_REQUEST.statusCode)
            get { hasEntity() }.isTrue()
        }
    }

    @Test
    fun mapGeneralError() {
        val exception = RuntimeException("Something went really wrong")

        val response = cut.mapGeneralError(exception)

        expectThat(response) {
            get { status }.isEqualTo(RestResponse.Status.INTERNAL_SERVER_ERROR.statusCode)
            get { hasEntity() }.isTrue()
        }
    }

    @Test
    fun mapNotFoundException() {
        val exception = NotFoundException("Book with id 1 not found")

        val response = cut.mapNotFoundException(exception)

        expectThat(response) {
            get { status }.isEqualTo(RestResponse.Status.NOT_FOUND.statusCode)
            get { hasEntity() }.isTrue()
        }
    }

    @Test
    fun mapPgException() {
        val exception = PersistenceException("Constraint x")

        val response = cut.mapPgException(exception)

        expectThat(response) {
            get { status }.isEqualTo(RestResponse.Status.CONFLICT.statusCode)
            get { hasEntity() }.isTrue()
        }
    }
}
