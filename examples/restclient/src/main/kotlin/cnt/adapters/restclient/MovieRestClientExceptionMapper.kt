package cnt.adapters.restclient

import cnt.exceptions.MovieNotFoundException
import cnt.exceptions.MovieServiceException
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper
import jakarta.ws.rs.core.Response

class MovieRestClientExceptionMapper: ResponseExceptionMapper<Exception> {
    override fun toThrowable(response: Response): Exception? =
        when (response.status) {
            in 500..599 -> throw MovieServiceException()
            404 -> throw MovieNotFoundException()
            else -> null
        }
}