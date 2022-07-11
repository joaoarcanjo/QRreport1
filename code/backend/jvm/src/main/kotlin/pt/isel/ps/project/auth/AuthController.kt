package pt.isel.ps.project.auth

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.project.auth.jwt.JwtBuilder.REMEMBER_ME_EXPIRATION
import pt.isel.ps.project.auth.jwt.JwtBuilder.buildJwt
import pt.isel.ps.project.auth.jwt.JwtConfig
import pt.isel.ps.project.model.Uris.Auth.LOGIN_PATH
import pt.isel.ps.project.model.Uris.Auth.LOGOUT_PATH
import pt.isel.ps.project.model.Uris.Auth.SIGNUP_PATH
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class AuthController(private val jwtConfig: JwtConfig, private val secretKey: SecretKey, private val authService: AuthService) {
    fun buildSessionCookie(token: String, maxAgeDays: Long) = ResponseCookie
        .from(SESSION_COOKIE_KEY, token)
        .sameSite("Strict")
        .maxAge(maxAgeDays * 24 * 60 * 60)
        .httpOnly(true)
        .secure(false)
        .build()
        .toString()

    fun getOriginRequestHeader(request: HttpServletRequest): String = request.getHeader(ORIGIN_HEADER)

    @PostMapping(SIGNUP_PATH)
    fun signup(@RequestBody signupDto: SignupDto, request: HttpServletRequest, response: HttpServletResponse) {
        val authPerson = authService.signup(signupDto)

        val token = buildJwt(
            authPerson.name,
            authPerson.toMap(),
            false,
            jwtConfig.tokenExpirationInDays,
            secretKey
        )

        when (getOriginRequestHeader(request)) {
            ORIGIN_MOBILE -> response.addHeader(jwtConfig.getAuthorizationHeader(), "${jwtConfig.tokenPrefix}$token")
            ORIGIN_WEBAPP -> response.addHeader(HttpHeaders.SET_COOKIE, buildSessionCookie(token, jwtConfig.tokenExpirationInDays))
        }
    }

    @PostMapping(LOGIN_PATH)
    fun login(@RequestBody loginDto: LoginDto, request: HttpServletRequest, response: HttpServletResponse) {
        val authPerson = authService.login(loginDto)

        val token = buildJwt(
            authPerson.name,
            authPerson.toMap(),
            loginDto.rememberMe,
            jwtConfig.tokenExpirationInDays,
            secretKey
        )

        when (getOriginRequestHeader(request)) {
            ORIGIN_MOBILE -> response.addHeader(jwtConfig.getAuthorizationHeader(), "${jwtConfig.tokenPrefix}$token")
            ORIGIN_WEBAPP -> response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildSessionCookie(token, if (loginDto.rememberMe) REMEMBER_ME_EXPIRATION else jwtConfig.tokenExpirationInDays)
            )
        }
    }

    @PostMapping(LOGOUT_PATH)
    fun logout(/*@RequestBody loginDto: LoginDto,*/ request: HttpServletRequest, response: HttpServletResponse) {
        when (getOriginRequestHeader(request)) {
            ORIGIN_WEBAPP -> response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildSessionCookie("null", 0)
            )
        }
    }
}