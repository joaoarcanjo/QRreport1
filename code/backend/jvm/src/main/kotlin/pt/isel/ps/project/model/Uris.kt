package pt.isel.ps.project.model

import org.springframework.web.util.UriTemplate
import java.util.*

object Uris {
    const val REPORT_FORM_URL = "http://localhost:3000/report/"
    const val VERSION = "/v1"

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
        const val BASE_PATH = "$VERSION/companies"
        const val SPECIFIC_PATH = "$BASE_PATH/{companyId}"
        const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
        const val DEACTIVATE_PATH = "$SPECIFIC_PATH/deactivate"
        const val COMPANIES_PAGINATION = "$BASE_PATH{?page}"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        private val DEACTIVATE_TEMPLATE = UriTemplate(DEACTIVATE_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("companyId" to id)).toString()
        fun makeActivate(id: Long) = ACTIVATE_TEMPLATE.expand(mapOf("companyId" to id)).toString()
        fun makeDeactivate(id: Long) = DEACTIVATE_TEMPLATE.expand(mapOf("companyId" to id)).toString()

        object Buildings {
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
        const val BASE_PATH = "$VERSION/tickets"
        const val SPECIFIC_PATH = "${BASE_PATH}/{ticketId}"
        const val STATE_PATH = "${SPECIFIC_PATH}/state"
        const val EMPLOYEE_PATH = "${SPECIFIC_PATH}/employee"
        const val RATE_PATH = "${SPECIFIC_PATH}/rate"
        const val TICKETS_PAGINATION = "$BASE_PATH{?page}"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val EMPLOYEE_TEMPLATE = UriTemplate(EMPLOYEE_PATH)
        private val RATE_TEMPLATE = UriTemplate(RATE_PATH)
        private val STATE_TEMPLATE = UriTemplate(STATE_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeEmployee(id: Long) = EMPLOYEE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeRate(id: Long) = RATE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeState(id: Long) = STATE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()

        object Comments {
            const val BASE_PATH = "${Tickets.SPECIFIC_PATH}/comments"
            const val SPECIFIC_PATH = "${BASE_PATH}/{commentId}"
            const val COMMENTS_PAGINATION = "$BASE_PATH{?page}"

            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            fun makeBase(ticketId: Long) = BASE_TEMPLATE.expand(mapOf("ticketId" to ticketId)).toString()
            fun makeSpecific(commentId: Long, ticketId: Long) =
                SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to ticketId, "commentId" to commentId)).toString()
        }
    }

    object Persons {
        const val BASE_PATH = "$VERSION/persons"
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
    }

    object Auth {
        const val SIGNUP_PATH = "$VERSION/signup"
        const val LOGIN_PATH = "$VERSION/login"
        const val LOGOUT_PATH = "$VERSION/logout"
    }

    private const val PAGINATION_PATH = "?page={pageIdx}"
    fun makePagination(page: Int, uri: String) =
        UriTemplate("$uri$PAGINATION_PATH").expand(mapOf("pageIdx" to page)).toString()
}