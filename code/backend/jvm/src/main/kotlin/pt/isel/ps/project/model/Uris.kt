package pt.isel.ps.project.model

import org.springframework.web.util.UriTemplate
import pt.isel.ps.project.model.Uris.Filters.makeBuildingId
import pt.isel.ps.project.model.Uris.Filters.makeCompanyId
import pt.isel.ps.project.model.Uris.Filters.makeDirection
import pt.isel.ps.project.model.Uris.Filters.makeEmployeeState
import pt.isel.ps.project.model.Uris.Filters.makeRole
import pt.isel.ps.project.model.Uris.Filters.makeSortBy
import java.util.*

object Uris {
    const val REPORT_FORM_URL = "http://localhost:3000/report/"
    const val VERSION = "/v1"
    const val UNDEFINED_ID = 0
    const val UNDEFINED_ID_LONG: Long = 0
    const val UNDEFINED = "null"
    const val DEFAULT_BOOL = false

    const val QUERY_STATE_KEY = "state"

    object Categories {
        const val BASE_PATH = "$VERSION/categories"
        const val SPECIFIC_PATH = "$BASE_PATH/{categoryId}"
        const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
        const val DEACTIVATE_PATH = "$SPECIFIC_PATH/deactivate"
        const val CATEGORIES_PAGINATION = "$BASE_PATH{?page}"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        private val DEACTIVATE_TEMPLATE = UriTemplate(DEACTIVATE_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("categoryId" to id)).toString()
        fun makeActivate(id: Long) = ACTIVATE_TEMPLATE.expand(mapOf("categoryId" to id)).toString()
        fun makeDeactivate(id: Long) = DEACTIVATE_TEMPLATE.expand(mapOf("categoryId" to id)).toString()
    }

    object Devices {
        const val BASE_PATH = "$VERSION/devices"
        const val SPECIFIC_PATH = "$BASE_PATH/{deviceId}"
        const val ACTIVATE_PATH ="$SPECIFIC_PATH/activate"
        const val DEACTIVATE_PATH ="$SPECIFIC_PATH/deactivate"
        const val CATEGORY_PATH ="$SPECIFIC_PATH/category"
        const val DEVICES_PAGINATION = "$BASE_PATH{?page}"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        private val DEACTIVATE_TEMPLATE = UriTemplate(DEACTIVATE_PATH)
        private val CATEGORY_TEMPLATE = UriTemplate(CATEGORY_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("deviceId" to id)).toString()
        fun makeActivate(id: Long) = ACTIVATE_TEMPLATE.expand(mapOf("deviceId" to id)).toString()
        fun makeDeactivate(id: Long) = DEACTIVATE_TEMPLATE.expand(mapOf("deviceId" to id)).toString()
        fun makeCategory(id: Long) = CATEGORY_TEMPLATE.expand(mapOf("deviceId" to id)).toString()

        object Anomalies {
            const val BASE_PATH = "${Devices.SPECIFIC_PATH}/anomalies"
            const val SPECIFIC_PATH = "$BASE_PATH/{anomalyId}"
            const val ANOMALIES_PAGINATION = "$BASE_PATH{?page}"

            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)

            fun makeBase(deviceId: Long) = BASE_TEMPLATE.expand(mapOf("deviceId" to deviceId)).toString()
            fun makeSpecific(deviceId: Long, id: Long) =
                SPECIFIC_TEMPLATE.expand(mapOf("deviceId" to deviceId, "anomalyId" to id)).toString()
        }
    }

    object Companies {
        const val QUERY_USER_KEY = "userId"
        const val QUERY_ASSIGN_KEY = "assign"
        const val QUERY_COMPANY_KEY = "company"

        const val BASE_PATH = "$VERSION/companies"
        const val SPECIFIC_PATH = "$BASE_PATH/{companyId}"
        const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
        const val DEACTIVATE_PATH = "$SPECIFIC_PATH/deactivate"
        const val COMPANIES_PAGINATION = "$BASE_PATH{?page}"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        private val DEACTIVATE_TEMPLATE = UriTemplate(DEACTIVATE_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("companyId" to id)).toString()
        fun makeSpecificWithPage(companyId: Long, page: Int) = makeSpecific(companyId) + "?page=${page}"
        fun makeSpecificPaginationTemplate(companyId: Long) = makeSpecific(companyId) + "{?page}"
        fun makeActivate(id: Long) = ACTIVATE_TEMPLATE.expand(mapOf("companyId" to id)).toString()
        fun makeDeactivate(id: Long) = DEACTIVATE_TEMPLATE.expand(mapOf("companyId" to id)).toString()

        fun companiesSelf(page: Int, userId: UUID?, state: String, assign: Boolean): String {
            var uri = makePagination(page, BASE_PATH)
            uri = makeCompanyUserId(userId, uri)
            uri = makeAssignCompany(assign, uri)
            return makeCompaniesState(state, uri)
        }

        fun companiesPagination(userId: UUID?, state: String, assign: Boolean): String {
            var uri = makeCompanyUserId(userId, "")
            uri = makeCompaniesState(state, uri)
            uri = makeAssignCompany(assign, uri)
            return "${COMPANIES_PAGINATION}$uri"
        }

        private const val ASSIGN_PATH = "&assign={assign}"

        private fun makeAssignCompany(assign: Boolean, uri: String): String {
            if (!assign) return uri
            return UriTemplate("$uri${ASSIGN_PATH}").expand(mapOf("assign" to assign)).toString()
        }

        private const val COMPANY_USER_PATH = "&userId={userId}"

        private fun makeCompanyUserId(userId: UUID?, uri: String): String {
            if (userId == null) return uri
            return UriTemplate("$uri${COMPANY_USER_PATH}").expand(mapOf("userId" to userId)).toString()
        }

        private const val COMPANY_STATE_PATH = "&state={state}"

        private fun makeCompaniesState(state: String, uri: String): String {
            if (state == UNDEFINED) return uri
            return UriTemplate("$uri${COMPANY_STATE_PATH}").expand(mapOf("state" to state)).toString()
        }

        object Buildings {
            const val QUERY_BUILDING_KEY = "building"

            const val BASE_PATH = "${Companies.SPECIFIC_PATH}/buildings"
            const val SPECIFIC_PATH = "$BASE_PATH/{buildingId}"
            const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
            const val DEACTIVATE_PATH = "$SPECIFIC_PATH/deactivate"
            const val MANAGER_PATH = "$SPECIFIC_PATH/manager"
            const val BUILDINGS_PAGINATION = "$BASE_PATH{?page}"

            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
            private val DEACTIVATE_TEMPLATE = UriTemplate(DEACTIVATE_PATH)
            private val MANAGER_TEMPLATE = UriTemplate(MANAGER_PATH)

            fun makeBase(companyId: Long) =
                BASE_TEMPLATE.expand(mapOf("companyId" to companyId)).toString()
            fun makeSpecific(companyId: Long, id: Long) =
                SPECIFIC_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeSpecificWithPage(companyId: Long, buildingId: Long, page: Int) =
                makeSpecific(companyId, buildingId) + "?page=${page}"
            fun makeSpecificPaginationTemplate(companyId: Long, buildingId: Long) =
                makeSpecific(companyId, buildingId) + "{?page}"
            fun makeActivate(companyId: Long, id: Long) =
                ACTIVATE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeDeactivate(companyId: Long, id: Long) =
                DEACTIVATE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeManager(companyId: Long, id: Long) =
                MANAGER_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()

            object Rooms {
                const val BASE_PATH = "${Buildings.SPECIFIC_PATH}/rooms"
                const val SPECIFIC_PATH = "$BASE_PATH/{roomId}"
                const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
                const val DEACTIVATE_PATH = "$SPECIFIC_PATH/deactivate"
                const val DEVICES_PATH = "$SPECIFIC_PATH/devices"
                const val SPECIFIC_DEVICE_PATH = "$DEVICES_PATH/{deviceId}"
                const val ROOMS_PAGINATION = "$BASE_PATH{?page}"
                const val ROOM_DEVICES_PAGINATION = "$DEVICES_PATH{?page}"

                private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
                private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
                private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
                private val DEACTIVATE_TEMPLATE = UriTemplate(DEACTIVATE_PATH)
                private val DEVICES_TEMPLATE = UriTemplate(DEVICES_PATH)
                private val SPECIFIC_DEVICE_TEMPLATE = UriTemplate(SPECIFIC_DEVICE_PATH)

                fun makeBase(companyId: Long, buildingId: Long) =
                    BASE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId)).toString()
                fun makeSpecific(companyId: Long, buildingId: Long, roomId: Long) =
                    SPECIFIC_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId, "roomId" to roomId)).toString()
                fun makeSpecificWithPage(companyId: Long, buildingId: Long, roomId: Long, page: Int) =
                    makeSpecific(companyId, buildingId, roomId) + "?page=${page}"
                fun makeSpecificPaginationTemplate(companyId: Long, buildingId: Long, roomId: Long) =
                    makeSpecific(companyId, buildingId, roomId) + "{?page}"
                fun makeActivate(companyId: Long, buildingId: Long, roomId: Long) =
                    ACTIVATE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId, "roomId" to roomId)).toString()
                fun makeDeactivate(companyId: Long, buildingId: Long, roomId: Long) =
                    DEACTIVATE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId, "roomId" to roomId)).toString()
                fun makeDevices(companyId: Long, buildingId: Long, roomId: Long) =
                    DEVICES_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId, "roomId" to roomId)).toString()
                fun makeSpecificDevice(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long) =
                    SPECIFIC_DEVICE_TEMPLATE.expand(
                        mapOf("companyId" to companyId, "buildingId" to buildingId, "roomId" to roomId, "deviceId" to deviceId)
                    ).toString()
            }
        }
    }

    object QRCode {
        const val BASE_PATH = "${Companies.Buildings.Rooms.SPECIFIC_DEVICE_PATH}/qrcode"
        const val REPORT_PATH = "$VERSION/report/{hash}"

        private val SPECIFIC_QRCODE_TEMPLATE = UriTemplate(BASE_PATH)
        private val REPORT_PATH_TEMPLATE = UriTemplate(REPORT_PATH)

        fun makeSpecific(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long) =
            SPECIFIC_QRCODE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId,
                "roomId" to roomId, "deviceId" to deviceId)).toString()
        fun makeReport(hash: String) = REPORT_PATH_TEMPLATE.expand(mapOf("hash" to hash)).toString()
    }

    object Tickets {
        const val QUERY_EMPLOYEE_STATE_KEY = "employeeState"

        const val BASE_PATH = "$VERSION/tickets"
        const val EMPLOYEE_STATES_PATH = "$BASE_PATH/states"
        const val SPECIFIC_PATH = "${BASE_PATH}/{ticketId}"
        const val STATE_PATH = "${SPECIFIC_PATH}/state"
        const val EMPLOYEE_PATH = "${SPECIFIC_PATH}/employee"
        const val RATE_PATH = "${SPECIFIC_PATH}/rate"
        const val GROUP_PATH = "${SPECIFIC_PATH}/group"
        const val TICKETS_PAGINATION = "$BASE_PATH{?page}"
        const val EMPLOYEE_STATES_PAGINATION = "$EMPLOYEE_STATES_PATH{?page}"
        const val TICKET_EMPLOYEES_PAGINATION = "${EMPLOYEE_PATH}{?page}"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val EMPLOYEE_TEMPLATE = UriTemplate(EMPLOYEE_PATH)
        private val RATE_TEMPLATE = UriTemplate(RATE_PATH)
        private val STATE_TEMPLATE = UriTemplate(STATE_PATH)
        private val GROUP_TEMPLATE = UriTemplate(GROUP_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeEmployee(id: Long) = EMPLOYEE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeRate(id: Long) = RATE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeState(id: Long) = STATE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeGroup(id: Long) = GROUP_TEMPLATE.expand(mapOf("ticketId" to id)).toString()

        fun employeesPagination(ticketId: Long): String {
            val uri = makeEmployee(ticketId)
            return "$uri{?page}"
        }

        fun ticketsSelf(page: Int, direction: String, sortBy: String, companyId: Long?, buildingId: Long?, employeeState: Int?): String {
            var uri = makePagination(page, BASE_PATH)
            uri = makeDirection(direction, uri)
            uri = makeSortBy(sortBy, uri)
            uri = makeCompanyId(companyId, uri)
            uri = makeBuildingId(buildingId, uri)
            return makeEmployeeState(employeeState, uri)
        }

        fun ticketsPagination(direction: String, sortBy: String, companyId: Long?, buildingId: Long?, employeeState: Int?): String {
            var uri = makeDirection(direction, "")
            uri = makeSortBy(sortBy, uri)
            uri = makeCompanyId(companyId, uri)
            uri = makeBuildingId(buildingId, uri)
            uri = makeEmployeeState(employeeState, uri)
            return "${Persons.PERSONS_PAGINATION}$uri"
        }

        object Comments {
            const val BASE_PATH = "${Tickets.SPECIFIC_PATH}/comments"
            const val SPECIFIC_PATH = "${BASE_PATH}/{commentId}"
            const val COMMENTS_PAGINATION = "$BASE_PATH{?page}"

            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            fun makeBase(ticketId: Long) = BASE_TEMPLATE.expand(mapOf("ticketId" to ticketId)).toString()
            fun makeSpecific(ticketId: Long, commentId: Long) =
                SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to ticketId, "commentId" to commentId)).toString()
        }
    }

    object Persons {
        const val QUERY_ROLE_KEY = "role"

        const val BASE_PATH = "$VERSION/persons"
        const val PROFILE_PATH = "$VERSION/profile"
        const val SPECIFIC_PATH = "$BASE_PATH/{personId}"
        private const val BASE_FIRE_PATH = "/persons/{personId}/fire"
        private const val BASE_REHIRE_PATH = "/persons/{personId}/rehire"
        const val FIRE_PATH = "${Companies.SPECIFIC_PATH}$BASE_FIRE_PATH"
        const val REHIRE_PATH = "${Companies.SPECIFIC_PATH}$BASE_REHIRE_PATH"
        const val BAN_PATH = "$SPECIFIC_PATH/ban"
        const val UNBAN_PATH = "$SPECIFIC_PATH/unban"
        const val ADD_ROLE_PATH = "$SPECIFIC_PATH/add-role"
        const val REMOVE_ROLE_PATH = "$SPECIFIC_PATH/remove-role"
        const val ADD_SKILL_PATH = "$SPECIFIC_PATH/add-skill"
        const val REMOVE_SKILL_PATH = "$SPECIFIC_PATH/remove-skill"
        const val ASSIGN_COMPANY_PATH = "$SPECIFIC_PATH/assign-company"
        const val PERSONS_PAGINATION = "$BASE_PATH{?page}"

        const val SWITCH_ROLE = "$VERSION/profile/switch-role"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val FIRE_TEMPLATE = UriTemplate(BASE_FIRE_PATH)
        private val REHIRE_TEMPLATE = UriTemplate(BASE_REHIRE_PATH)
        private val BAN_TEMPLATE = UriTemplate(BAN_PATH)
        private val UNBAN_TEMPLATE = UriTemplate(UNBAN_PATH)
        private val ADD_ROLE_TEMPLATE = UriTemplate(ADD_ROLE_PATH)
        private val REMOVE_ROLE_TEMPLATE = UriTemplate(REMOVE_ROLE_PATH)
        private val ADD_SKILL_TEMPLATE = UriTemplate(ADD_SKILL_PATH)
        private val REMOVE_SKILL_TEMPLATE = UriTemplate(REMOVE_SKILL_PATH)
        private val ASSIGN_COMPANY_TEMPLATE = UriTemplate(ASSIGN_COMPANY_PATH)
        private val SWITCH_ROLE_TEMPLATE = UriTemplate(SWITCH_ROLE)
        fun makeSpecific(id: UUID) = SPECIFIC_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeFire(personId: UUID) = Companies.SPECIFIC_PATH + FIRE_TEMPLATE.expand(mapOf("personId" to personId)).toString()
        fun makeRehire(personId: UUID) = Companies.SPECIFIC_PATH + REHIRE_TEMPLATE.expand(mapOf("personId" to personId)).toString()
        fun makeBan(id: UUID) = BAN_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeUnban(id: UUID) = UNBAN_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeAddRole(id: UUID) = ADD_ROLE_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeRemoveRole(id: UUID) = REMOVE_ROLE_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeAddSkill(id: UUID) = ADD_SKILL_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeRemoveSkill(id: UUID) = REMOVE_SKILL_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeAssignCompany(id: UUID) = ASSIGN_COMPANY_TEMPLATE.expand(mapOf("personId" to id)).toString()

        fun personsSelf(page: Int, companyId: Long?, role: String): String {
            var uri = makePagination(page, BASE_PATH)
            uri = makeCompanyId(companyId, uri)
            return makeRole(role, uri)
        }

        fun personsPagination(companyId: Long?, role: String): String {
            var uri = makeCompanyId(companyId, "")
            uri = makeRole(role, uri)
            return "$PERSONS_PAGINATION$uri"
        }
    }

    object Auth {
        const val SIGNUP_PATH = "$VERSION/signup"
        const val LOGIN_PATH = "$VERSION/login"
        const val LOGOUT_PATH = "$VERSION/logout"
    }

    object Filters {

        private const val ROLE_PATH = "&role={roleName}"

        fun makeRole(role: String, uri: String): String {
            if (role == UNDEFINED) return uri
            return UriTemplate("$uri$ROLE_PATH").expand(mapOf("roleName" to role)).toString()
        }

        private const val COMPANY_PATH = "&company={companyId}"

        fun makeCompanyId(companyId: Long?, uri: String): String {
            if (companyId == null) return uri
            return UriTemplate("$uri$COMPANY_PATH").expand(mapOf("companyId" to companyId)).toString()
        }

        private const val BUILDING_PATH = "&building={buildingId}"

        fun makeBuildingId(buildingId: Long?, uri: String): String {
            if (buildingId == null) return uri
            return UriTemplate("$uri$BUILDING_PATH").expand(mapOf("buildingId" to buildingId)).toString()
        }

        private const val DIRECTION_PATH = "&direction={direction}"

        fun makeDirection(direction: String, uri: String): String {
            return UriTemplate("$uri$DIRECTION_PATH").expand(mapOf("direction" to direction)).toString()
        }

        private const val SORT_BY_PATH = "&sortBy={sort}"

        fun makeSortBy(sortBy: String, uri: String): String {
            return UriTemplate("$uri$SORT_BY_PATH").expand(mapOf("sort" to sortBy)).toString()
        }

        private const val EMPLOYEE_STATE = "&employeeState={state}"

        fun makeEmployeeState(state: Int?, uri: String): String {
            if (state == null) return uri
            return UriTemplate("$uri$EMPLOYEE_STATE").expand(mapOf("state" to state)).toString()
        }
    }

    private const val PAGINATION_PATH = "?page={pageIdx}"
    fun makePagination(page: Int, uri: String) =
        UriTemplate("$uri$PAGINATION_PATH").expand(mapOf("pageIdx" to page)).toString()
}