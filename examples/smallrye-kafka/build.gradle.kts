dependencies {
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging-kafka")
    implementation("org.jetbrains.kotlin:kotlin-noarg")
    testImplementation("io.smallrye.reactive:smallrye-reactive-messaging-in-memory")
    testImplementation("io.quarkus:quarkus-test-kafka-companion")
}

noArg {
    annotation("cnt.kafka.configuration.AddDefaultNoArgConstructor")
}
