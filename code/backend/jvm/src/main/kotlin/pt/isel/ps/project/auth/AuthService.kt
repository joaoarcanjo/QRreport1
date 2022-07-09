package pt.isel.ps.project.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.INVALID_CREDENTIALS
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.exception.UnauthorizedException
import pt.isel.ps.project.util.Validator.Auth.Signup.verifySignupInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class AuthService(private val authDao: AuthDao, private val passwordEncoder: PasswordEncoder) {

    fun signup(signupDto: SignupDto): AuthPerson {
        verifySignupInput(signupDto)
        val authPerson = authDao.signup(signupDto, passwordEncoder.encode(signupDto.password))
            .getString(AUTH_REP)?.deserializeJsonTo<AuthPerson>()
        return authPerson ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun login(loginDto: LoginDto): AuthPerson {
        val credentials = authDao.getCredentials(loginDto.email).deserializeJsonTo<CredentialsEntity>()
        if (!passwordEncoder.matches(loginDto.password, credentials.password))
            throw UnauthorizedException(INVALID_CREDENTIALS)
        return authDao.login(credentials.email).deserializeJsonTo()
    }
}