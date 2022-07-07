package pt.isel.ps.project.auth.jwt

import io.jsonwebtoken.Jwts
import java.sql.Date
import java.time.LocalDate
import javax.crypto.SecretKey

object JwtBuilder {
    const val REMEMBER_ME_EXPIRATION = 30L // Days

    fun buildJwt(
        subject: String,
        claimsMap: Map<String, Any?>,
        rememberMe: Boolean,
        defaultExpiration: Long,
        secretKey: SecretKey
    ): String = Jwts.builder()
            .setSubject(subject)
            .setClaims(claimsMap)
            .setIssuedAt(java.util.Date())
            .setExpiration(Date.valueOf(LocalDate.now().plusDays(if (rememberMe) REMEMBER_ME_EXPIRATION else defaultExpiration)))
            .signWith(secretKey)
            .compact()
}