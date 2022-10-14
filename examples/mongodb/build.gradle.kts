val testcontainersVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-mongodb-panache")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:mongodb:$testcontainersVersion")
}