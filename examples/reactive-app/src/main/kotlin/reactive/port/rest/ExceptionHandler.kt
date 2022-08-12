package reactive.port.rest

import org.hibernate.HibernateException
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.server.ServerExceptionMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.PersistenceException
import javax.ws.rs.NotFoundException

class RestExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)

    @ServerExceptionMapper
    fun mapDuplicateItem(exception: HibernateException): RestResponse<ErrorMessage> {
        return RestResponse.ResponseBuilder
            .create<ErrorMessage>(RestResponse.Status.BAD_REQUEST.statusCode)
            .entity(ErrorMessage(exception.message))
            .build()
    }

    @ServerExceptionMapper
    fun mapGeneralError(throwable: Throwable): RestResponse<ErrorMessage> {
        logger.error(throwable.stackTraceToString())

        return RestResponse.ResponseBuilder
            .create<ErrorMessage>(RestResponse.Status.INTERNAL_SERVER_ERROR.statusCode)
            .entity(ErrorMessage(throwable.message))
            .build()
    }

    @ServerExceptionMapper
    fun mapNotFoundException(notFoundException: NotFoundException): RestResponse<ErrorMessage> {
        return RestResponse.ResponseBuilder
            .create<ErrorMessage>(RestResponse.Status.NOT_FOUND.statusCode)
            .entity(ErrorMessage(notFoundException.message))
            .build()
    }

    @ServerExceptionMapper
    fun mapPgException(persistenceException: PersistenceException): RestResponse<ErrorMessage> {
        return RestResponse.ResponseBuilder
            .create<ErrorMessage>(RestResponse.Status.CONFLICT.statusCode)
            .entity(ErrorMessage(persistenceException.message))
            .build()
    }

    data class ErrorMessage(val message: String?)
}
