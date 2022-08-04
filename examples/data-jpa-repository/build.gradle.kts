val testcontainersVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")

    implementation("io.quarkus:quarkus-jdbc-postgresql")

    testImplementation("io.quarkus:quarkus-jdbc-h2")
    testImplementation("io.quarkus:quarkus-test-h2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}
