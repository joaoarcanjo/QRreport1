package pt.isel.ps.project.auth.jwt

import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
@ConfigurationPropertiesScan
class JwtSecretKey(private val jwtConfig: JwtConfig) {
    @Bean
    fun secretKey(): SecretKey {
        return Keys.hmacShaKeyFor(jwtConfig.secretKey.toByteArray())
    }
}