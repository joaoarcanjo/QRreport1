package pt.isel.ps.project.auth.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.http.HttpHeaders

@ConstructorBinding
@ConfigurationProperties(prefix = "app.jwt")
data class JwtConfig(
    val secretKey: String,
    val tokenPrefix: String,
    val tokenExpirationInDays: Long,
) {
    fun getAuthorizationHeader() = HttpHeaders.AUTHORIZATION
}