@file:Suppress("UNCHECKED_CAST")

package pt.isel.ps.project.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import pt.isel.ps.project.auth.AuthCompanies
import pt.isel.ps.project.auth.AuthCompany
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.INVALID_TOKEN
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.REQUIRES_AUTH
import pt.isel.ps.project.exception.UnauthorizedException
import pt.isel.ps.project.util.deserializeJsonTo
import pt.isel.ps.project.util.serializeToJson
import java.sql.Timestamp
import java.util.*
import javax.crypto.SecretKey
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap


object JwtValidator {
    fun getJwtFromAuthorizationHeader(authorizationHeader: String, tokenPrefix: String): String? {
        return if (authorizationHeader.startsWith(tokenPrefix))
            authorizationHeader.replace(tokenPrefix, "")
        else null
    }

    fun validateJwtAndGetData(jwt: String, secretKey: SecretKey): AuthPerson {
        try {
            val claimsJws: Jws<Claims> = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)

            val body = claimsJws.body

            return AuthPerson(
                UUID.fromString(body["id"] as String),
                body.get("name", String::class.java),
                body.get("phone", String::class.java),
                body.get("email", String::class.java),
                body.get("activeRole", String::class.java),
                body["skills"] as ArrayList<String>?,
                body["companies"] as ArrayList<LinkedHashMap<*,*>>?,
                Timestamp(body["timestamp"] as Long),
                body.get("state", String::class.java),
                body.get("reason", String::class.java),
            )
        } catch (e: JwtException) {
            throw UnauthorizedException(REQUIRES_AUTH, INVALID_TOKEN)
        }
    }
}