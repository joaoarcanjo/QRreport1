package pt.isel.ps.project.config

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import pt.isel.ps.project.dao.*

@ConstructorBinding
@EnableTransactionManagement
@ConfigurationProperties(prefix = "app.datasource")
data class DatabaseSource(val connectionString: String)

@Configuration
@ConfigurationPropertiesScan
class DatabaseConfig(private val db: DatabaseSource) {

    @Bean //TODO retirar o bean annotattion
    fun jdbi(): Jdbi = Jdbi.create(db.connectionString).apply {
        installPlugin(KotlinSqlObjectPlugin())
        installPlugin(PostgresPlugin())
        installPlugin(KotlinPlugin())
    }

    @Bean
    fun buildingDao(): BuildingDao = jdbi().onDemand()

    @Bean
    fun commentDao(): CommentDao = jdbi().onDemand()

    @Bean
    fun ticketDao(): TicketDao = jdbi().onDemand()

    @Bean
    fun roomDao(): RoomDao = jdbi().onDemand()

    @Bean
    fun qrHashDao(): QRCodeDao = jdbi().onDemand()

    @Bean
    fun categoryDao(): CategoryDao = jdbi().onDemand()
}