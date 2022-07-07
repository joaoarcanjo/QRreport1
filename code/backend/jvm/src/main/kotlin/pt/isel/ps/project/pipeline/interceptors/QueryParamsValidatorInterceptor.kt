package pt.isel.ps.project.pipeline.interceptors

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.ps.project.exception.Errors.BadRequest.Locations.QUERY_STRING
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Pagination.PAGE_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.TYPE_MISMATCH_REQ_QUERY
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QUERY_PAGE_KEY
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class QueryParamsValidatorInterceptor: HandlerInterceptor {
    private fun throwInvalidParameterException() {
        throw InvalidParameterException(
            TYPE_MISMATCH_REQ_QUERY,
            listOf(InvalidParameter(QUERY_PAGE_KEY, QUERY_STRING, PAGE_TYPE_MISMATCH))
        )
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        try {
            val page = request.getParameter(QUERY_PAGE_KEY)?.toInt() ?: DEFAULT_PAGE

            if (page <= 0) throwInvalidParameterException()
        } catch (e: NumberFormatException) {
            throwInvalidParameterException()
        }
        return true
    }
}