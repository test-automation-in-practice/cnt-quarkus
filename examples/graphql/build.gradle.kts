val testcontainersVersion: String by project

dependencies {
    implementation("io.quarkus:quarkus-smallrye-graphql")

    testImplementation("io.quarkus:quarkus-smallrye-graphql-client")
}

noArg {
    annotation("cnt.graphql.business.configuration.AddDefaultNoArgConstructor")
}