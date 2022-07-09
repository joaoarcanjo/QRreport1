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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.EnableTransactionManagement
import pt.isel.ps.project.auth.AuthDao
import pt.isel.ps.project.dao.*

@ConstructorBinding
@EnableTransactionManagement
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

    @Bean
    fun companyDao(): CompanyDao = jdbi().onDemand()

    @Bean
    fun personDao(): PersonDao = jdbi().onDemand()

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

    @Bean
    fun deviceDao(): DeviceDao = jdbi().onDemand()

    @Bean
    fun anomalyDao(): AnomalyDao = jdbi().onDemand()

    @Bean
    fun authDao(): AuthDao = jdbi().onDemand()

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}