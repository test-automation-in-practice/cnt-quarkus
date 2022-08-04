plugins {
    kotlin("jvm") 
    kotlin("plugin.allopen")
    id("io.quarkus")
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val assertjVersion: String by project

val jvmVersion: String by project

allprojects {

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.allopen")
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
        testImplementation("io.quarkus:quarkus-junit5")
        testImplementation("org.assertj:assertj-core:$assertjVersion")

    }

    java.sourceCompatibility = JavaVersion.toVersion(jvmVersion)
    java.targetCompatibility = JavaVersion.toVersion(jvmVersion)

    allOpen {
        annotation("javax.ws.rs.Path")
        annotation("javax.enterprise.context.ApplicationScoped")
        annotation("io.quarkus.test.junit.QuarkusTest")
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