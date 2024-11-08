package cnt.reactive.flyway

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.flywaydb.core.Flyway

@ApplicationScoped
class FlywayConfiguration(
    @ConfigProperty(name = "quarkus.datasource.reactive.url") private val databaseUrl: String,
    @ConfigProperty(name = "quarkus.datasource.username") private val databaseUsername: String,
    @ConfigProperty(name = "quarkus.datasource.password") private val databasePassword: String,
) {

    fun config(@Observes ev: StartupEvent) {
        /**
         * Flyway requires a blocking JDBC connection to execute migrations.
         * This is why we replace the reactive vertx-reactive datasource with JDBC, providing Flyway with a non-reactive JDBC connection to the database.
         */
        Flyway.configure()
            .dataSource(databaseUrl.replace("vertx-reactive", "jdbc"), databaseUsername, databasePassword)
            .locations("db/migration")
            .baselineOnMigrate(true)
            .load()
            .migrate()
    }
}
