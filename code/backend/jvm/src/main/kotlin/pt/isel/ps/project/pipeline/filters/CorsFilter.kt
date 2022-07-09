package pt.isel.ps.project.pipeline.filters

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import pt.isel.ps.project.auth.ORIGIN_HEADER
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CorsFilter : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // In case there isn't a preflight request, continue...
        if (request.method != "OPTIONS") filterChain.doFilter(request, response)

        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000")
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, HEAD")
        response.addHeader(
            "Access-Control-Allow-Headers",
            "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, " +
                    "Access-Control-Request-Headers, $ORIGIN_HEADER"
        )
        response.addHeader(
            "Access-Control-Expose-Headers",
            "Access-Control-Allow-Origin, Access-Control-Allow-Credentials"
        )
        response.addHeader("Access-Control-Allow-Credentials", "true")
        response.addIntHeader("Access-Control-Max-Age", 60)
        response.status = 200
    }
}