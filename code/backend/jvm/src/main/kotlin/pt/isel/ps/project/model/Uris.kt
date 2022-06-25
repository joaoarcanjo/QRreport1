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

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("categoryId" to id)).toString()
        fun makeActivate(id: Long) = ACTIVATE_TEMPLATE.expand(mapOf("categoryId" to id)).toString()
    }

    object Devices {
        const val BASE_PATH = "$VERSION/devices"
        const val SPECIFIC_PATH = "$BASE_PATH/{deviceId}"
        const val ACTIVATE_PATH ="$SPECIFIC_PATH/activate"
        const val CATEGORY_PATH ="$SPECIFIC_PATH/category"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
        private val CATEGORY_TEMPLATE = UriTemplate(CATEGORY_PATH)
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("deviceId" to id)).toString()
        fun makeActivate(id: Long) = ACTIVATE_TEMPLATE.expand(mapOf("deviceId" to id)).toString()
        fun makeCategory(id: Long) = CATEGORY_TEMPLATE.expand(mapOf("deviceId" to id)).toString()

        object Anomalies {
            const val BASE_PATH = "${Devices.SPECIFIC_PATH}/anomalies"
            const val SPECIFIC_PATH = "$BASE_PATH/{anomalyId}"

            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)

            fun makeBase(deviceId: Long) = //TODO make test to this function
                BASE_TEMPLATE.expand(mapOf("deviceId" to deviceId)).toString()
            fun makeSpecific(deviceId: Long, id: Long) =
                SPECIFIC_TEMPLATE.expand(mapOf("deviceId" to deviceId, "anomalyId" to id)).toString()
        }
    }

    object Companies {
        const val BASE_PATH = "$VERSION/companies"
        const val SPECIFIC_PATH = "$BASE_PATH/{companyId}"
        const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
        const val DEACTIVATE_PATH = "$SPECIFIC_PATH/deactivate"

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
            const val MANAGER_PATH = "$SPECIFIC_PATH/manager"

            private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
            private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
            private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
            private val MANAGER_TEMPLATE = UriTemplate(MANAGER_PATH)

            fun makeBase(companyId: Long) =
                BASE_TEMPLATE.expand(mapOf("companyId" to companyId)).toString()
            fun makeSpecific(companyId: Long, id: Long) =
                SPECIFIC_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeActivate(companyId: Long, id: Long) =
                ACTIVATE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()
            fun makeManager(companyId: Long, id: Long) =
                MANAGER_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to id)).toString()

            object Rooms {
                const val BASE_PATH = "${Buildings.SPECIFIC_PATH}/rooms"
                //TODO questionar se o caminho para o especifico room não deveria incluir a company e building
                const val SPECIFIC_PATH = "$VERSION/rooms/{roomId}"
                //TODO update documentation and change deactivate path to delete method
                const val ACTIVATE_PATH = "$SPECIFIC_PATH/activate"
                const val DEVICES_PATH = "$SPECIFIC_PATH/devices"
                const val SPECIFIC_DEVICE_PATH = "$DEVICES_PATH/{deviceId}"

                private val BASE_TEMPLATE = UriTemplate(BASE_PATH)
                private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
                private val ACTIVATE_TEMPLATE = UriTemplate(ACTIVATE_PATH)
                private val DEVICES_TEMPLATE = UriTemplate(DEVICES_PATH)
                private val SPECIFIC_DEVICE_TEMPLATE = UriTemplate(SPECIFIC_DEVICE_PATH)

                fun makeBase(companyId: Long, buildingId: Long) = //todo test
                    BASE_TEMPLATE.expand(mapOf("companyId" to companyId, "buildingId" to buildingId)).toString()
                fun makeSpecific(id: Long) =
                    SPECIFIC_TEMPLATE.expand(mapOf("roomId" to id)).toString()
                fun makeActivate(id: Long) =
                    ACTIVATE_TEMPLATE.expand(mapOf("roomId" to id)).toString()
                fun makeDevices(id: Long) =
                    DEVICES_TEMPLATE.expand(mapOf("roomId" to id)).toString()
                fun makeSpecificDevice(roomId: Long, deviceId: Long) =
                    SPECIFIC_DEVICE_TEMPLATE.expand(mapOf("roomId" to roomId, "deviceId" to deviceId)).toString()

                object QRCode {
                    const val BASE_PATH = "$SPECIFIC_DEVICE_PATH/qrcode"

                    private val SPECIFIC_QRCODE_TEMPLATE = UriTemplate(BASE_PATH)

                    fun makeSpecific(roomId: Long, deviceId: Long) =
                        SPECIFIC_QRCODE_TEMPLATE.expand(mapOf("roomId" to roomId, "deviceId" to deviceId)).toString()
                }
            }
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
        fun makeSpecific(id: Long) = SPECIFIC_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeEmployee(id: Long) = EMPLOYEE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()
        fun makeRate(id: Long) = RATE_TEMPLATE.expand(mapOf("ticketId" to id)).toString()

        object Comments {
            const val BASE_PATH = "${Tickets.SPECIFIC_PATH}/comments"
            const val SPECIFIC_PATH = "${BASE_PATH}/{commentId}"

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
        const val FIRE_PATH = "${Companies.SPECIFIC_PATH}/persons/{personId}/fire"
        const val REHIRE_PATH = "${Companies.SPECIFIC_PATH}/persons/{personId}/rehire"
        const val BAN_PATH = "$SPECIFIC_PATH/ban"
        const val UNBAN_PATH = "$SPECIFIC_PATH/unban"
        const val ADD_ROLE_PATH = "$SPECIFIC_PATH/add-role"
        const val REMOVE_ROLE_PATH = "$SPECIFIC_PATH/remove-role"
        const val ADD_SKILL_PATH = "$SPECIFIC_PATH/add-skill"
        const val REMOVE_SKILL_PATH = "$SPECIFIC_PATH/remove-skill"
        const val ASSIGN_COMPANY_PATH = "$SPECIFIC_PATH/assign-company"

        private val SPECIFIC_TEMPLATE = UriTemplate(SPECIFIC_PATH)
        private val FIRE_TEMPLATE = UriTemplate(FIRE_PATH)
        private val REHIRE_TEMPLATE = UriTemplate(REHIRE_PATH)
        private val BAN_TEMPLATE = UriTemplate(BAN_PATH)
        private val UNBAN_TEMPLATE = UriTemplate(UNBAN_PATH)
        private val ADD_ROLE_TEMPLATE = UriTemplate(ADD_ROLE_PATH)
        private val REMOVE_ROLE_TEMPLATE = UriTemplate(REMOVE_ROLE_PATH)
        private val ADD_SKILL_TEMPLATE = UriTemplate(ADD_SKILL_PATH)
        private val REMOVE_SKILL_TEMPLATE = UriTemplate(REMOVE_SKILL_PATH)
        private val ASSIGN_COMPANY_TEMPLATE = UriTemplate(ASSIGN_COMPANY_PATH)
        fun makeSpecific(id: UUID) = SPECIFIC_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeFire(companyId: Long, personId: UUID) = FIRE_TEMPLATE.expand(mapOf("companyId" to companyId, "personId" to personId)).toString()
        fun makeRehire(companyId: Long, personId: UUID) = REHIRE_TEMPLATE.expand(mapOf("companyId" to companyId, "personId" to personId)).toString()
        fun makeBan(id: UUID) = BAN_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeUnban(id: UUID) = UNBAN_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeAddRole(id: UUID) = ADD_ROLE_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeRemoveRole(id: UUID) = REMOVE_ROLE_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeAddSkill(id: UUID) = ADD_SKILL_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeRemoveSkill(id: UUID) = REMOVE_SKILL_TEMPLATE.expand(mapOf("personId" to id)).toString()
        fun makeAssignCompany(id: UUID) = ASSIGN_COMPANY_TEMPLATE.expand(mapOf("personId" to id)).toString()
    }

    private const val PAGINATION_PATH = "?page={pageIdx}"
    fun makePagination(page: Int, uri: String) =
        UriTemplate("$uri$PAGINATION_PATH").expand(mapOf("pageIdx" to page)).toString()
}