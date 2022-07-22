package pt.isel.ps.project.pipeline.interceptors

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.ps.project.exception.Errors.BadRequest.Locations.QUERY_STRING
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.ASSIGN_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.BUILDING_QUERY_ID_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.COMPANY_QUERY_ID_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Direction.DIRECTION_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Pagination.PAGE_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Person.INVALID_UUID_FORMAT
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Role.ROLE_QUERY_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Sort.SORT_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.State.STATE_TYPE_MISMATCH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.TYPE_MISMATCH_REQ_QUERY
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.EMPLOYEE_STATE_TYPE_MISMATCH
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.Uris.Companies.Buildings.QUERY_BUILDING_KEY
import pt.isel.ps.project.model.Uris.Companies.QUERY_ASSIGN_KEY
import pt.isel.ps.project.model.Uris.Companies.QUERY_COMPANY_KEY
import pt.isel.ps.project.model.Uris.Companies.QUERY_USER_KEY
import pt.isel.ps.project.model.Uris.Persons.QUERY_ROLE_KEY
import pt.isel.ps.project.model.Uris.QUERY_STATE_KEY
import pt.isel.ps.project.model.Uris.Tickets.QUERY_EMPLOYEE_STATE_KEY
import pt.isel.ps.project.model.Uris.UNDEFINED
import pt.isel.ps.project.model.Uris.UNDEFINED_ID
import pt.isel.ps.project.model.Uris.UNDEFINED_ID_LONG
import pt.isel.ps.project.model.person.Roles
import pt.isel.ps.project.model.person.Roles.ADMIN
import pt.isel.ps.project.model.person.Roles.EMPLOYEE
import pt.isel.ps.project.model.person.Roles.MANAGER
import pt.isel.ps.project.model.person.Roles.USER
import pt.isel.ps.project.model.representations.*
import pt.isel.ps.project.model.state.States
import pt.isel.ps.project.model.state.States.ACTIVE
import pt.isel.ps.project.model.state.States.INACTIVE
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class QueryParamsValidatorInterceptor: HandlerInterceptor {

    private fun throwInvalidParameterException(key: String, message: String) {
        throw InvalidParameterException(
            TYPE_MISMATCH_REQ_QUERY,
            listOf(InvalidParameter(key, QUERY_STRING, message))
        )
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
       try {
            val page = request.getParameter(QUERY_PAGE_KEY)?.toInt() ?: DEFAULT_PAGE

            if (page <= 0) throwInvalidParameterException(QUERY_PAGE_KEY, PAGE_TYPE_MISMATCH)
        } catch (e: NumberFormatException) {
            throwInvalidParameterException(QUERY_PAGE_KEY, PAGE_TYPE_MISMATCH)
        }

        val direction = request.getParameter(QUERY_DIRECTION_KEY) ?: DEFAULT_DIRECTION
        if (direction != "asc" && direction != "desc") throwInvalidParameterException(QUERY_DIRECTION_KEY, DIRECTION_TYPE_MISMATCH)

        val sortBy = request.getParameter(QUERY_SORT_KEY) ?: DEFAULT_SORT
        if (sortBy != "name" && sortBy != "date") throwInvalidParameterException(QUERY_SORT_KEY, SORT_TYPE_MISMATCH)

        try {
            val company = request.getParameter(QUERY_COMPANY_KEY)?.toLong() ?: UNDEFINED_ID_LONG
            if (company < 0) throwInvalidParameterException(QUERY_COMPANY_KEY, COMPANY_QUERY_ID_TYPE_MISMATCH)
        } catch (e: NumberFormatException) {
            throwInvalidParameterException(QUERY_COMPANY_KEY,  COMPANY_QUERY_ID_TYPE_MISMATCH)
        }

        try {
            val building = request.getParameter(QUERY_BUILDING_KEY)?.toLong() ?: UNDEFINED_ID_LONG
            if (building < 0) throwInvalidParameterException(QUERY_BUILDING_KEY, BUILDING_QUERY_ID_TYPE_MISMATCH)
        } catch (e: NumberFormatException) {
            throwInvalidParameterException(QUERY_BUILDING_KEY, BUILDING_QUERY_ID_TYPE_MISMATCH)
        }

        try {
            val employeeState = request.getParameter(QUERY_EMPLOYEE_STATE_KEY)?.toInt() ?: UNDEFINED_ID
            if (employeeState < 0) throwInvalidParameterException(QUERY_EMPLOYEE_STATE_KEY, EMPLOYEE_STATE_TYPE_MISMATCH)
        } catch (e: NumberFormatException) {
            throwInvalidParameterException(QUERY_EMPLOYEE_STATE_KEY, EMPLOYEE_STATE_TYPE_MISMATCH)
        }

        val role = request.getParameter(QUERY_ROLE_KEY) ?: UNDEFINED
        if (role != MANAGER && role != ADMIN && role != EMPLOYEE && role != USER && role != UNDEFINED)
            throwInvalidParameterException(QUERY_ROLE_KEY, ROLE_QUERY_TYPE_MISMATCH)

        val user = request.getParameter(QUERY_USER_KEY) ?: UNDEFINED
        if (user != UNDEFINED) {
            try {
                UUID.fromString(user)
            }catch (e: IllegalArgumentException) {
                throwInvalidParameterException(QUERY_USER_KEY, INVALID_UUID_FORMAT)
            }
        }

        val state = request.getParameter(QUERY_STATE_KEY) ?: UNDEFINED
        if (state != ACTIVE && state != INACTIVE && state != UNDEFINED)
            throwInvalidParameterException(QUERY_STATE_KEY, STATE_TYPE_MISMATCH)

        //assign parameter validation
        request.getParameter(QUERY_ASSIGN_KEY).toBoolean()

        return true
    }
}