val wiremockVersion: String by project
val pactVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-rest-client-reactive")
    implementation("io.quarkus:quarkus-rest-client-reactive-jackson")

    // See https://github.com/quarkiverse/quarkus-pact/issues/28; for dev mode tests, the scope cannot be test
    testImplementation("io.quarkiverse.pact:quarkus-pact-consumer:$pactVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wiremockVersion")
}