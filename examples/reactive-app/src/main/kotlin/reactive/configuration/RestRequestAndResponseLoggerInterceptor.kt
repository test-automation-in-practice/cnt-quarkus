package reactive.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.ext.Provider

@Provider
class RestRequestAndResponseLoggerInterceptor : ContainerRequestFilter, ContainerResponseFilter {

    private val log: Logger = LoggerFactory.getLogger(RestRequestAndResponseLoggerInterceptor::class.java)

    private companion object {
        const val REQUEST_PATTERN: String = "Request => {} {}"
        const val RESPONSE_PATTERN: String = "Response <= {}"
    }

    override fun filter(requestContext: ContainerRequestContext) {
        val method = requestContext.method
        val path = requestContext.uriInfo.path

        log.info(REQUEST_PATTERN, method, path)
    }

    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val status = responseContext.status

        log.info(RESPONSE_PATTERN, status)
    }
}

