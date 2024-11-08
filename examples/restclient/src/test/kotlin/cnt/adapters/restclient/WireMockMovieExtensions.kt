package cnt.adapters.restclient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import jakarta.ws.rs.core.MediaType

fun WireMockServer.stubGetMovies(statusCode: Int, responseBody: String? = null) {
    if (responseBody == null) {
        stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/movies")).willReturn(WireMock.aResponse().withStatus(statusCode))
        )
    } else {
        stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/movies")).willReturn(
                WireMock.aResponse().withStatus(statusCode).withBody(responseBody)
                    .withHeader("Content-type", MediaType.APPLICATION_JSON)
            )
        )
    }
}

fun WireMockServer.stubGetMovieById(movieId: String, statusCode: Int, responseBody: String? = null) {
    if (responseBody == null) {
        stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/movie/${movieId}"))
                .willReturn(WireMock.aResponse().withStatus(statusCode))
        )
    } else {
        stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/movie/${movieId}")).willReturn(
                WireMock.aResponse().withStatus(statusCode).withBody(responseBody)
                    .withHeader("Content-type", MediaType.APPLICATION_JSON)
            )
        )
    }
}

fun WireMockServer.stubAddMovie(
    statusCode: Int, requestPayload: String, responseBody: String? = null
) {
    if (responseBody == null) {
        stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/movie")).withRequestBody(WireMock.equalToJson(requestPayload))
                .willReturn(WireMock.aResponse().withStatus(statusCode))
        )
    } else {
        stubFor(
            WireMock.post(WireMock.urlPathEqualTo("/movie")).withRequestBody(WireMock.equalToJson(requestPayload))
                .willReturn(
                    WireMock.aResponse().withStatus(statusCode).withBody(responseBody)
                        .withHeader("Content-type", MediaType.APPLICATION_JSON)
                )
        )
    }
}

fun WireMockServer.stubTimeout(serviceEndpoint: String) {
    stubFor(
        WireMock.any(WireMock.urlPathEqualTo(serviceEndpoint)).willReturn(WireMock.aResponse().withFixedDelay(1500))
    )
}

fun WireMockServer.verifyGetMockServerRequest(serviceEndpoint: String) {
    verify(
        WireMock.getRequestedFor(WireMock.urlEqualTo(serviceEndpoint))
    )
}

fun WireMockServer.verifyPostMockServerRequest(
    serviceEndpoint: String, expectedRequestBody: String
) {
    verify(
        WireMock.postRequestedFor(WireMock.urlEqualTo(serviceEndpoint)).withRequestBody(
            WireMock.equalToJson(
                expectedRequestBody
            )
        )
    )
}
