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

        object Buildings {
            const val BASE_PATH = "${Companies.SPECIFIC_PATH}/buildings"
            const val SPECIFIC_PATH = "$BASE_PATH/{buildingId}"
            const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
            const val MANAGER_PATH = "$SPECIFIC_PATH/manager"

            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
            private val MANAGER_TEMPLATE = UriTemplate(MANAGER_PATH)
            fun makeSpecific(companyId: Int, id: Int) =
                SPECIFIC_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeActivate(companyId: Int, id: Int) =
                ACTIVATE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeManager(companyId: Int, id: Int) =
                MANAGER_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
        }
    }

    object Tickets {
        const val BASE_PATH = "$VERSION/tickets"
        const val SPECIFIC_PATH = "${BASE_PATH}/{ticketId}"
        const val STATE_PATH = "${SPECIFIC_PATH}/state"
        const val EMPLOYEE_PATH = "${SPECIFIC_PATH}/employee"
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

            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            fun makeBase(ticketId: Int) = BASE_TEMPLATE.expand(mapOf("ticketId" to ticketId)).toString()
            fun makeSpecific(commentId: Int, ticketId: Int) =
                SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to ticketId, "commentId" to commentId)).toString()
        }
    }
}