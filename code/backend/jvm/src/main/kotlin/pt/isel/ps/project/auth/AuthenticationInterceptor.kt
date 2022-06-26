package pt.isel.ps.project.auth

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.ps.project.auth.jwt.JwtConfig
import pt.isel.ps.project.auth.jwt.JwtValidator.getJwt
import pt.isel.ps.project.auth.jwt.JwtValidator.validateJwtAndGetData
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.AUTHORIZATION_HEADER_MISSING
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.INVALID_TYPE
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.REQUIRES_AUTH
import pt.isel.ps.project.exception.UnauthorizedException
import pt.isel.ps.project.util.Validator.Ticket.AccessWithoutAuth.isAuthURI
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@ConfigurationPropertiesScan
class AuthenticationInterceptor(private val jwtConfig: JwtConfig, private val secretKey: SecretKey): HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        // Verify if its login or signup, if so we don't need to authenticate
        if (isAuthURI(request.requestURI)) return true

        // Verify if the token exists and if its valid
        val authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader())
            ?: throw UnauthorizedException(REQUIRES_AUTH, AUTHORIZATION_HEADER_MISSING)

        val jwt = getJwt(authorizationHeader, jwtConfig.tokenPrefix)
            ?: throw UnauthorizedException(REQUIRES_AUTH, INVALID_TYPE)

        val authPerson = validateJwtAndGetData(jwt, secretKey)
        request.setAttribute(REQ_ATTRIBUTE_AUTHPERSON, authPerson)

        return true
    }
}