package pt.isel.ps.project.auth

import java.sql.Timestamp
import java.util.*

/**
 * Name of the auth representation output parameter
 */
const val AUTH_REP = "authRep"

data class LoginDto(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false,
)

data class CredentialsEntity(
    val email: String,
    val password: String,
)

object SignupEntity {
    const val SIGNUP_NAME = "name"
    const val SIGNUP_PHONE = "phone"
    const val SIGNUP_EMAIL = "email"
    const val SIGNUP_PASSWORD = "password"
    const val SIGNUP_CONFIRM_PASSWORD = "confirmPassword"

    const val PASSWORD_MAX_LENGTH = 128
}

data class SignupDto(
    val name: String,
    val phone: String?,
    val email: String,
    val password: String,
    val confirmPassword: String,
)

const val REQ_ATTRIBUTE_AUTHPERSON = "AuthPerson"
data class AuthPerson(
    val id: UUID,
    val name: String,
    val phone: String?,
    val email: String,
    val activeRole: String,
    val skills: List<String>?,
    val companies: List<String>?,
    val timestamp: Timestamp,
    val state: String,
    val reason: String?,
)

fun AuthPerson.toMap() = HashMap<String, Any?>()
    .apply {
        put("id", id)
        put("name", name)
        put("phone", phone)
        put("email", email)
        put("activeRole", activeRole)
        put("skills", skills)
        put("companies", companies)
        put("timestamp", timestamp)
        put("state", state)
        put("reason", reason)
}