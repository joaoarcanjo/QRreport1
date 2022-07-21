package pt.isel.ps.project.auth

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.ps.project.auth.jwt.JwtConfig
import pt.isel.ps.project.auth.jwt.JwtValidator.getJwtFromAuthorizationHeader
import pt.isel.ps.project.auth.jwt.JwtValidator.validateJwtAndGetData
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.AUTHORIZATION_HEADER_MISSING
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.INVALID_ORIGIN
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.INVALID_TYPE
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.MISSING_SESSION_COOKIE
import pt.isel.ps.project.exception.Errors.Unauthorized.Message.REQUIRES_AUTH
import pt.isel.ps.project.exception.UnauthorizedException
import pt.isel.ps.project.util.Validator.AccessWithoutAuth.isAuthURI
import pt.isel.ps.project.util.Validator.AccessWithoutAuth.isCreatePersonURI
import pt.isel.ps.project.util.Validator.AccessWithoutAuth.isCreateTicketURI
import pt.isel.ps.project.util.Validator.AccessWithoutAuth.isReportURI
import javax.crypto.SecretKey
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@ConfigurationPropertiesScan
class AuthenticationInterceptor(private val jwtConfig: JwtConfig, private val secretKey: SecretKey): HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Verify if the origin header exists
        val originReqHeader = request.getHeader(ORIGIN_HEADER)
        if (originReqHeader == null || (originReqHeader.compareTo(ORIGIN_MOBILE) != 0 && originReqHeader.compareTo(ORIGIN_WEBAPP) != 0)) {
            if (isReportURI(request.requestURI) || isCreateTicketURI(request.requestURI, request.method)) return true
            else throw UnauthorizedException(REQUIRES_AUTH, INVALID_ORIGIN)
        }

        // Verify if its login or signup, if so we don't need to authenticate
        if (isAuthURI(request.requestURI)) return true

        // Verify if the token exists and if its valid
        val jwt: String = when (originReqHeader) {
            ORIGIN_MOBILE -> {
                val authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader())
                    ?: throw UnauthorizedException(REQUIRES_AUTH, AUTHORIZATION_HEADER_MISSING)
                getJwtFromAuthorizationHeader(authorizationHeader, jwtConfig.tokenPrefix)
                    ?: throw UnauthorizedException(REQUIRES_AUTH, INVALID_TYPE)
            }
            ORIGIN_WEBAPP -> {
                val sessionCookie = request.cookies.firstOrNull { it.name.compareTo("session") == 0 }
                    ?: throw UnauthorizedException(REQUIRES_AUTH, MISSING_SESSION_COOKIE)
                sessionCookie.value
            }
            else -> throw UnauthorizedException(REQUIRES_AUTH, INVALID_ORIGIN)
        }

        val authPerson = validateJwtAndGetData(jwt, secretKey)
        request.setAttribute(REQ_ATTRIBUTE_AUTHPERSON, authPerson)

        return true
    }
}