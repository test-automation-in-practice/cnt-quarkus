plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")

    id("io.quarkus")
}

val jvmVersion: String by project

val assertjVersion: String by project
val mockkVersion: String by project
val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val quarkusMockkVersion: String by project

allprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.allopen")
        plugin("org.jetbrains.kotlin.plugin.noarg")
        plugin("io.quarkus")
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
        implementation("io.quarkus:quarkus-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("io.quarkus:quarkus-arc")
        implementation("io.quarkus:quarkus-config-yaml")

        testImplementation("io.quarkus:quarkus-junit5")
        testImplementation("io.quarkiverse.mockk:quarkus-junit5-mockk:$quarkusMockkVersion")
        testImplementation("org.assertj:assertj-core:$assertjVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")
    }

    java.sourceCompatibility = JavaVersion.toVersion(jvmVersion)
    java.targetCompatibility = JavaVersion.toVersion(jvmVersion)

    allOpen {
        annotation("javax.ws.rs.Path")
        annotation("javax.enterprise.context.ApplicationScoped")
        annotation("io.quarkus.test.junit.QuarkusTest")
    }

    noArg {
        annotation("io.quarkus.mongodb.panache.common.MongoEntity")
    }

    tasks {

        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = jvmVersion
            kotlinOptions.javaParameters = true
        }

        withType<Test> {

            testLogging {
                showExceptions = true
                showStandardStreams = true
                events(
                    org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
                )
            }
        }
    }
}