# Quarkus RestClient

Showcase demonstrating how to test a RestClient with WireMock or with PACT.

As the reactive RestClient should be a drop in replacement for the _normal_ RestClient we use in our
showcase the reactive RestClient

You should decide for yourself if you want to test your Client with PACT or with Wiremock. You don't need both :-)

## Wiremock

### TestResource and Annotation

To separate each test responses for your testcase you need to inject the started WireMockServer into
your testcase.

For this override the `inject` Method in your Extension and define an Annotation.

```kotlin
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

    // Maps the Annotation InjectWireMockServer to the local variable wireMockServer
    // and allows you in the testcase to define your responses
    override fun inject(testInjector: TestInjector) {
        testInjector.injectIntoFields(
            wireMockServer,
            AnnotatedAndMatchesType(InjectWireMockServer::class.java, WireMockServer::class.java)
        )
    }
}
```

```kotlin
@Target(FIELD, VALUE_PARAMETER)
@Retention
annotation class InjectWireMockServer()
```

### Attention

If you specify one `@QuarkusTest` with `@QuarkusTestResource`, it will be __applied to all__ your
Tests which have a `@QuarkusTest` annotation.

If you specify a `@TestProfile`, the WireMockExtension is applied to all Tests with this annotation. 

### Test

```kotlin
@Test
fun `should get all Movies, when getAllMovies Method is called`() {

    val expectedResult = Movies(listOf(...))
    val responePayload = """{...}""".trimIndent()
    wireMockServer.stubGetMovies(200, responePayload)

    val actualResult = movieRestClient.getAllMovies()

    actualResult assertEqualsComparingFieldByField expectedResult
    wireMockServer.verifyMockServerRequest("/movies")
}
```

As you can see, there is a clear separation of preparing the test, execute the rest call and comparing the result

### WireMockTestProfile

To combine PACT and Wiremock we created a TestProfile for Wiremock Tests. This is not needed when you only have WiremockTests or PACT Tests.

## PACT

When you want to use PACT in DEV-Mode (quarkusDev) the PACT-Extension has to be on compile Level

```kotlin
dependencies {
    implementation("io.quarkus:quarkus-rest-client-reactive")
    implementation("io.quarkus:quarkus-rest-client-reactive-jackson")
    // See https://github.com/quarkiverse/quarkus-pact/issues/28; for dev mode tests, the scope cannot be test
    implementation("io.quarkiverse.pact:quarkus-pact-consumer:0.2")

    testImplementation("com.github.tomakehurst:wiremock-jre8:$wiremockVersion")
}
```
