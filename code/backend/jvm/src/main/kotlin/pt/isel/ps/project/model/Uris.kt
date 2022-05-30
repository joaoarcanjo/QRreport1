package pt.isel.ps.project.model

import org.springframework.web.util.UriTemplate

object Uris {
    const val VERSION = "/v1"

    object Companies {
        const val BASE_PATH = "$VERSION/companies"
        const val SPECIFIC_PATH = "$BASE_PATH/{companyId}"
        const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        fun makeSpecific(id: Int) = SPECIFIC_TEMPLATE.expand(mapOf("companyId" to id)).toString()
        fun makeActivate(id: Int) = ACTIVATE_TEMPLATE.expand(mapOf("companyId" to id)).toString()
    }

    object Tickets {
        const val BASE_PATH = "$VERSION/tickets"
        const val SPECIFIC_PATH = "${BASE_PATH}/{ticketId}"
        const val STATE_PATH = "${SPECIFIC_PATH}/state"
        const val EMPLOYEE_PATH = "${SPECIFIC_PATH}/employee"
        const val REMOVE_EMPLOYEE_PATH = "${EMPLOYEE_PATH}/remove"
        const val RATE_PATH = "${SPECIFIC_PATH}/rate"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val EMPLOYEE_TEMPLATE = UriTemplate(EMPLOYEE_PATH)
        private val RATE_TEMPLATE = UriTemplate(RATE_PATH)
        fun makeSpecific(id: Int) = SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeEmployee(id: Int) = EMPLOYEE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeRate(id: Int) = RATE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()

        object Comments {
            const val BASE_PATH = "${Tickets.SPECIFIC_PATH}/comments"
            const val SPECIFIC_PATH = "${BASE_PATH}/{commentId}"
        }
    }
}