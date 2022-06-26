package pt.isel.ps.project.auth

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.project.auth.jwt.JwtBuilder.buildJwt
import pt.isel.ps.project.auth.jwt.JwtConfig
import pt.isel.ps.project.model.Uris.Auth.LOGIN_PATH
import pt.isel.ps.project.model.Uris.Auth.SIGNUP_PATH
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletResponse

@RestController
class AuthController(private val jwtConfig: JwtConfig, private val secretKey: SecretKey, private val authService: AuthService) {
    @PostMapping(SIGNUP_PATH)
    fun signup(@RequestBody signupDto: SignupDto, response: HttpServletResponse) {
        val authPerson = authService.signup(signupDto)

        val token = buildJwt(
            authPerson.name,
            authPerson.toMap(),
            false,
            jwtConfig.tokenExpirationInDays,
            secretKey
        )

        response.addHeader(jwtConfig.getAuthorizationHeader(), "${jwtConfig.tokenPrefix}$token")
    }

    @PostMapping(LOGIN_PATH)
    fun login(@RequestBody loginDto: LoginDto, response: HttpServletResponse) {
        val authPerson = authService.login(loginDto)

        val token = buildJwt(
            authPerson.name,
            authPerson.toMap(),
            loginDto.rememberMe,
            jwtConfig.tokenExpirationInDays,
            secretKey
        )

        response.addHeader(jwtConfig.getAuthorizationHeader(), "${jwtConfig.tokenPrefix}$token")
    }
}