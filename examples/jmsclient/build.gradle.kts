val qpidJmsVersion: String by project
val qpidJmsTestVersion: String by project

dependencies {
    implementation("org.amqphub.quarkus:quarkus-qpid-jms:$qpidJmsVersion")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")

    testImplementation("io.quarkiverse.artemis:quarkus-test-artemis:$qpidJmsTestVersion")
    testImplementation("io.rest-assured:rest-assured")

}