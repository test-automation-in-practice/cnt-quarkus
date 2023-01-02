package util

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager.TestInjector.AnnotatedAndMatchesType
import javax.ws.rs.core.MediaType.APPLICATION_JSON

class WireMockExtensions : QuarkusTestResourceLifecycleManager {

    private val wireMockServer = WireMockServer(
        WireMockConfiguration.wireMockConfig().dynamicPort().httpsPort(-1).keystorePath(null)
    )

    override fun start(): MutableMap<String, String> {
        wireMockServer.start()

        return mutableMapOf("""quarkus.rest-client."cnt.adapters.restclient.MovieRestClient".url""" to wireMockServer.baseUrl())
    }

    override fun stop() {
        wireMockServer.stop()
    }

    override fun inject(testInjector: TestInjector) {
        testInjector.injectIntoFields(
            wireMockServer,
            AnnotatedAndMatchesType(InjectWireMockServer::class.java, WireMockServer::class.java)
        )
    }
}
