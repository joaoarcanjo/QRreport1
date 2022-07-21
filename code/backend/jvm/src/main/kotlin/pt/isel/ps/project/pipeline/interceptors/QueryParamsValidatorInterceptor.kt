package pt.isel.ps.project.pipeline.interceptors

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.ps.project.exception.Errors.BadRequest.Locations.QUERY_STRING
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.COMPANY_QUERY_ID_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Direction.DIRECTION_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Pagination.PAGE_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Role.ROLE_QUERY_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Sort.SORT_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.TYPE_MISMATCH_REQ_QUERY
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.Uris.Persons.QUERY_COMPANY_KEY
import pt.isel.ps.project.model.Uris.Persons.QUERY_ROLE_KEY
import pt.isel.ps.project.model.Uris.UNDEFINED
import pt.isel.ps.project.model.Uris.UNDEFINED_ID
import pt.isel.ps.project.model.person.Roles
import pt.isel.ps.project.model.person.Roles.ADMIN
import pt.isel.ps.project.model.person.Roles.EMPLOYEE
import pt.isel.ps.project.model.person.Roles.MANAGER
import pt.isel.ps.project.model.person.Roles.USER
import pt.isel.ps.project.model.representations.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class QueryParamsValidatorInterceptor: HandlerInterceptor {

    private fun throwInvalidParameterException(message: String) {
        throw InvalidParameterException(
            TYPE_MISMATCH_REQ_QUERY,
            listOf(InvalidParameter(QUERY_PAGE_KEY, QUERY_STRING, message))
        )
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
       try {
            val page = request.getParameter(QUERY_PAGE_KEY)?.toInt() ?: DEFAULT_PAGE

            if (page <= 0) throwInvalidParameterException(PAGE_TYPE_MISMATCH)
        } catch (e: NumberFormatException) {
            throwInvalidParameterException(PAGE_TYPE_MISMATCH)
        }

        val direction = request.getParameter(QUERY_DIRECTION_KEY) ?: DEFAULT_DIRECTION
        if (direction != "asc" && direction != "desc") throwInvalidParameterException(DIRECTION_TYPE_MISMATCH)

        val sortBy = request.getParameter(QUERY_SORT_KEY) ?: DEFAULT_SORT
        if (sortBy != "name" && sortBy != "date") throwInvalidParameterException(SORT_TYPE_MISMATCH)

        try {
            val company = request.getParameter(QUERY_COMPANY_KEY)?.toInt() ?: UNDEFINED_ID
            if (company < 0) throwInvalidParameterException(COMPANY_QUERY_ID_TYPE_MISMATCH)
        } catch (e: NumberFormatException) {
            throwInvalidParameterException(COMPANY_QUERY_ID_TYPE_MISMATCH)
        }

        val role = request.getParameter(QUERY_ROLE_KEY) ?: UNDEFINED
        if (role != MANAGER && role != ADMIN && role != EMPLOYEE && role != USER && role != UNDEFINED)
            throwInvalidParameterException(ROLE_QUERY_TYPE_MISMATCH)

        return true
    }
}