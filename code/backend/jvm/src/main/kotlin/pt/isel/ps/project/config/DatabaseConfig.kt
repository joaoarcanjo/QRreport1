package pt.isel.ps.project.config

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConstructorBinding
@ConfigurationProperties(prefix = "app.datasource")
data class DatabaseSource(val connectionString: String)

@Configuration
@ConfigurationPropertiesScan
class DatabaseConfig(private val db: DatabaseSource) {

    @Bean
    fun jdbi(): Jdbi = Jdbi.create(db.connectionString).apply {
        installPlugin(KotlinSqlObjectPlugin())
        installPlugin(PostgresPlugin())
        installPlugin(KotlinPlugin())
    }
}