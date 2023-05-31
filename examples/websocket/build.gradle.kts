dependencies {
    implementation("io.quarkus:quarkus-websockets")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("org.jetbrains.kotlin:kotlin-noarg")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

noArg {
    annotation("cnt.websocket.NoArgConstructor")
}
