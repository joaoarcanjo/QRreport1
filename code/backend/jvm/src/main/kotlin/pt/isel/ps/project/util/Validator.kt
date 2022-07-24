package pt.isel.ps.project.util

import org.springframework.http.HttpMethod
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.SignupDto
import pt.isel.ps.project.auth.SignupEntity.SIGNUP_CONFIRM_PASSWORD
import pt.isel.ps.project.auth.SignupEntity.SIGNUP_PASSWORD
import pt.isel.ps.project.exception.Errors.BadRequest.Locations
import pt.isel.ps.project.exception.Errors.BadRequest.Message.ADD_EMPLOYEE_MANAGER_ROLE_WITHOUT_COMPANY
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Auth.PASSWORD_MISMATCH_REASON
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Auth.PASSWORD_MISMATCH_TITLE
import pt.isel.ps.project.exception.Errors.BadRequest.Message.BLANK_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.BLANK_PARAMS_DETAIL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.CREATE_EMPLOYEE_WITHOUT_SKILL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Category.INVALID_CATEGORY_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_FLOOR_NUMBER
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_FLOOR_NUMBER
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.INVALID_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Device.Anomaly.INVALID_ANOMALY_ANOMALY_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Device.INVALID_DEVICE_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.EMPLOYEE_NULL_SKILL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.INVALID_REQ_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.NULL_EMPLOYEE_MANAGER_COMPANY
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.Comment.INVALID_COMMENT_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_DESCRIPTION_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_HASH_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_RATE
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_SUBJECT_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.UPDATE_NULL_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.UPDATE_NULL_PARAMS_DETAIL
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.MANAGER_CREATE_PERSON
import pt.isel.ps.project.exception.Errors.Forbidden.Message.MANAGER_CREATE_PERSON_COMPANY
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Auth.LOGIN_PATH
import pt.isel.ps.project.model.Uris.Auth.SIGNUP_PATH
import pt.isel.ps.project.model.Uris.QRCode.REPORT_PATH
import pt.isel.ps.project.model.anomaly.AnomalyEntity.ANOMALY_ANOMALY
import pt.isel.ps.project.model.anomaly.AnomalyEntity.ANOMALY_ANOMALY_MAX_CHARS
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.building.BuildingEntity.BUILDING_FLOORS
import pt.isel.ps.project.model.building.BuildingEntity.BUILDING_MAX_FLOORS_NUMBER
import pt.isel.ps.project.model.building.BuildingEntity.BUILDING_NAME
import pt.isel.ps.project.model.building.BuildingEntity.BUILDING_NAME_MAX_CHARS
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.category.CategoryEntity.CATEGORY_NAME
import pt.isel.ps.project.model.category.CategoryEntity.CATEGORY_NAME_MAX_CHARS
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.model.comment.CommentEntity.COMMENT
import pt.isel.ps.project.model.comment.CommentEntity.COMMENT_MAX_CHARS
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.model.company.CompanyEntity.COMPANY_NAME
import pt.isel.ps.project.model.company.CompanyEntity.COMPANY_NAME_MAX_CHARS
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.DeviceEntity.DEVICE_NAME
import pt.isel.ps.project.model.device.DeviceEntity.DEVICE_NAME_MAX_CHARS
import pt.isel.ps.project.model.device.UpdateDeviceEntity
import pt.isel.ps.project.model.person.AddRoleToPersonEntity
import pt.isel.ps.project.model.person.CreatePersonEntity
import pt.isel.ps.project.model.person.PersonDto
import pt.isel.ps.project.model.person.PersonEntity.COMPANY
import pt.isel.ps.project.model.person.PersonEntity.SKILL
import pt.isel.ps.project.model.person.Roles.ADMIN
import pt.isel.ps.project.model.person.Roles.EMPLOYEE
import pt.isel.ps.project.model.person.Roles.GUEST
import pt.isel.ps.project.model.person.Roles.MANAGER
import pt.isel.ps.project.model.person.Roles.USER
import pt.isel.ps.project.model.person.UpdatePersonEntity
import pt.isel.ps.project.model.room.CreateRoomEntity
import pt.isel.ps.project.model.room.RoomEntity.MAX_FLOOR
import pt.isel.ps.project.model.room.RoomEntity.MIN_FLOOR
import pt.isel.ps.project.model.room.RoomEntity.ROOM_FLOOR
import pt.isel.ps.project.model.room.RoomEntity.ROOM_NAME
import pt.isel.ps.project.model.room.RoomEntity.ROOM_NAME_MAX_CHARS
import pt.isel.ps.project.model.room.UpdateRoomEntity
import pt.isel.ps.project.model.state.States.ACTIVE
import pt.isel.ps.project.model.state.States.BANNED
import pt.isel.ps.project.model.state.States.INACTIVE
import pt.isel.ps.project.model.ticket.CreateTicketEntity
import pt.isel.ps.project.model.ticket.TicketEntity.MAX_RATE
import pt.isel.ps.project.model.ticket.TicketEntity.MIN_RATE
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_DESCRIPTION
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_DESCRIPTION_MAX_CHARS
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_HASH
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_RATE
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_SUBJECT
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_SUBJECT_MAX_CHARS
import pt.isel.ps.project.model.ticket.TicketExtraInfo
import pt.isel.ps.project.model.ticket.TicketRateEntity
import pt.isel.ps.project.model.ticket.UpdateTicketEntity
import java.util.*
import kotlin.collections.ArrayList

object Validator {

    private fun checkIfIsNotBlank(input: String, parameterName: String) {
        if (input.isBlank()) {
            throw InvalidParameterException(
                BLANK_PARAMS_DETAIL,
                listOf(InvalidParameter(parameterName, Locations.BODY, BLANK_PARAMS))
            )
        }
    }

    object Device {

        private fun checkNameLength(name: String) {
            if (name.length > DEVICE_NAME_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(DEVICE_NAME, Locations.BODY, INVALID_DEVICE_NAME_LENGTH))
            )
        }

        /*
         * Verify if the device name inserted is valid.
         */
        fun verifyCreateDeviceInput(device: CreateDeviceEntity): Boolean {
            checkIfIsNotBlank(device.name, DEVICE_NAME)
            checkNameLength(device.name)
            return true
        }

        /*
         * Verify if the device name to update is valid.
         */
        fun verifyUpdateDeviceInput(device: UpdateDeviceEntity): Boolean {
            checkIfIsNotBlank(device.name, DEVICE_NAME)
            checkNameLength(device.name)
            return true
        }

        object Anomaly {

            private fun checkAnomalyLength(anomaly: String) {
                if (anomaly.length > ANOMALY_ANOMALY_MAX_CHARS) throw InvalidParameterException(
                    INVALID_REQ_PARAMS,
                    listOf(InvalidParameter(ANOMALY_ANOMALY, Locations.BODY, INVALID_ANOMALY_ANOMALY_LENGTH))
                )
            }

            /*
             * Verify if the anomaly inserted is valid.
             */
            fun verifyAnomalyInput(anomaly: InputAnomalyEntity): Boolean {
                checkIfIsNotBlank(anomaly.anomaly, ANOMALY_ANOMALY)
                checkAnomalyLength(anomaly.anomaly)
                return true
            }
        }
    }

    object Category {

        private fun checkNameLength(name: String) {
            if (name.length > CATEGORY_NAME_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(CATEGORY_NAME, Locations.BODY, INVALID_CATEGORY_NAME_LENGTH))
            )
        }

        /*
         * Verify if the category name inserted is valid.
         */
        fun verifyCategoryInput(category: InputCategoryEntity): Boolean {
            checkIfIsNotBlank(category.name, CATEGORY_NAME)
            checkNameLength(category.name)
            return true
        }
    }

    object Company {
        private fun checkNameLength(name: String) {
            if (name.length > COMPANY_NAME_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(COMPANY_NAME, Locations.BODY, INVALID_NAME_LENGTH))
            )
        }

        private fun checkIfAllUpdatableParametersAreNull(company: UpdateCompanyEntity) {
            if (company.name == null) throw InvalidParameterException(
                UPDATE_NULL_PARAMS,
                detail = UPDATE_NULL_PARAMS_DETAIL,
            )
        }

        fun verifyCreateCompanyInput(company: CreateCompanyEntity): Boolean {
            checkNameLength(company.name)
            return true
        }

        fun verifyUpdateCompanyInput(company: UpdateCompanyEntity): Boolean {
            checkIfAllUpdatableParametersAreNull(company)
            checkNameLength(company.name!!)
            return true
        }

        fun personBelongsToCompany(user: AuthPerson, currentCompany: Long): Boolean {
            return user.companies?.first { (it["id"].toString()).toLong() == currentCompany } != null
        }

        object Building {

            private fun checkNameLength(name: String) {
                if (name.length > BUILDING_NAME_MAX_CHARS) throw InvalidParameterException(
                    INVALID_REQ_PARAMS,
                    listOf(InvalidParameter(BUILDING_NAME, Locations.BODY, INVALID_BUILDING_NAME_LENGTH))
                )
            }

            private fun checkFloorsNumber(floors: Int) {
                if (floors > BUILDING_MAX_FLOORS_NUMBER) throw InvalidParameterException(
                    INVALID_REQ_PARAMS,
                    listOf(InvalidParameter(BUILDING_FLOORS, Locations.BODY, INVALID_BUILDING_FLOOR_NUMBER))
                )
            }

            /*
             * Verify if parameters to create building are valid.
             */
            fun verifyCreateBuildingInput(building: CreateBuildingEntity): Boolean {
                checkIfIsNotBlank(building.name, BUILDING_NAME)
                checkNameLength(building.name)
                checkFloorsNumber(building.floors)
                return true
            }

            /*
             * Verify if parameters to update building are valid.
             */
            fun verifyUpdateBuildingInput(building: UpdateBuildingEntity): Boolean {
                var bothEmptyFlag = true

                building.name?.let {
                    checkIfIsNotBlank(it, BUILDING_NAME)
                    checkNameLength(it)
                    bothEmptyFlag = false
                }
                building.floors?.let {
                    checkFloorsNumber(it)
                    bothEmptyFlag = false
                }
                if (bothEmptyFlag) {
                    throw InvalidParameterException(UPDATE_NULL_PARAMS, detail = UPDATE_NULL_PARAMS_DETAIL)
                }
                return true
            }

            object Room {

                private fun checkNameLength(name: String) {
                    if (name.length > ROOM_NAME_MAX_CHARS) throw InvalidParameterException(
                        INVALID_REQ_PARAMS,
                        listOf(InvalidParameter(ROOM_NAME, Locations.BODY, INVALID_ROOM_NAME_LENGTH))
                    )
                }

                private fun checkFloorNumber(floor: Int) {
                    if (floor !in MIN_FLOOR..MAX_FLOOR) throw InvalidParameterException(
                        INVALID_REQ_PARAMS,
                        listOf(InvalidParameter(ROOM_FLOOR, Locations.BODY, INVALID_ROOM_FLOOR_NUMBER))
                    )
                }

                /*
                 * Verify if parameters to create room are valid.
                 */
                fun verifyCreateRoomInput(room: CreateRoomEntity): Boolean {
                    checkIfIsNotBlank(room.name, ROOM_NAME)
                    checkNameLength(room.name)
                    checkFloorNumber(room.floor)
                    return true
                }

                /*
                 * Verify if parameters to update room are valid.
                 */
                fun verifyUpdateRoomInput(room: UpdateRoomEntity): Boolean {
                    checkIfIsNotBlank(room.name, ROOM_NAME)
                    checkNameLength(room.name)
                    return true
                }
            }
        }
    }

    object Ticket {

        private fun checkSubjectLength(subject: String) {
            if (subject.length > TICKET_SUBJECT_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_SUBJECT, Locations.BODY, INVALID_SUBJECT_LENGTH))
            )
        }

        private fun checkDescriptionLength(description: String) {
            if (description.length > TICKET_DESCRIPTION_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_DESCRIPTION, Locations.BODY, INVALID_DESCRIPTION_LENGTH))
            )
        }

        private fun checkHashLength(hash: String) {
            if (hash.length != Hash.MD5.HEXA_HASH_SIZE) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_HASH, Locations.BODY, INVALID_HASH_LENGTH))
            )
        }

        private fun checkRate(rate: Int) {
            if (rate !in MIN_RATE..MAX_RATE) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_RATE, Locations.BODY, INVALID_RATE))
            )
        }

        /*
         * Verify if parameters to create ticket are valid.
         */
        fun verifyCreateTicketInput(ticket: CreateTicketEntity): Boolean {
            checkIfIsNotBlank(ticket.description, TICKET_DESCRIPTION)
            checkIfIsNotBlank(ticket.subject, TICKET_SUBJECT)
            checkIfIsNotBlank(ticket.hash, TICKET_HASH)
            checkDescriptionLength(ticket.description)
            checkSubjectLength(ticket.subject)
            checkHashLength(ticket.hash)
            return true
        }

        /*
         * Verify if parameters to update ticket are valid.
         */
        fun verifyUpdateTicketInput(ticket: UpdateTicketEntity): Boolean {
            var bothEmptyFlag = true

            ticket.subject?.let {
                checkIfIsNotBlank(it, TICKET_SUBJECT)
                checkSubjectLength(it)
                bothEmptyFlag = false
            }
            ticket.description?.let {
                checkIfIsNotBlank(it, TICKET_DESCRIPTION)
                checkDescriptionLength(it)
                bothEmptyFlag = false
            }
            if (bothEmptyFlag) {
                throw InvalidParameterException(UPDATE_NULL_PARAMS, detail = UPDATE_NULL_PARAMS_DETAIL)
            }
            return true
        }

        /*
         * Verify if the rate is between 0 and 5 inclusive.
         */
        fun verifyTicketRateInput(ticketRate: TicketRateEntity): Boolean {
            checkRate(ticketRate.rate)
            return true
        }

        fun isTicketRated(ticketInfo: TicketExtraInfo): Boolean {
            return ticketInfo.ticket.rate != null
        }

        object Comment {

            private fun checkCommentLength(comment: String) {
                if (comment.length > COMMENT_MAX_CHARS) throw InvalidParameterException(
                    INVALID_REQ_PARAMS,
                    listOf(InvalidParameter(COMMENT, Locations.BODY, INVALID_COMMENT_LENGTH))
                )
            }

            /*
             * Verify if the comment inserted is valid.
             */
            fun verifyCommentInput(comment: CreateCommentEntity): Boolean {
                checkIfIsNotBlank(comment.comment, COMMENT)
                checkCommentLength(comment.comment)
                return true
            }
        }
    }

    object Person {
        fun verifyCreatePersonInput(person: CreatePersonEntity): Boolean {
            // Employees must be linked to a skill
            if (person.role == EMPLOYEE && person.skill == null) {
                throw InvalidParameterException(
                    CREATE_EMPLOYEE_WITHOUT_SKILL,
                    listOf(InvalidParameter(SKILL, Locations.BODY, EMPLOYEE_NULL_SKILL))
                )
            }
            return true
        }

        fun verifyManagerCreationPermissions(user: AuthPerson, person: CreatePersonEntity) {
            // Verify same company
            if (user.companies?.firstOrNull { it["id"].toString().toLong() == person.company } == null)
                throw ForbiddenException(ACCESS_DENIED, MANAGER_CREATE_PERSON_COMPANY)
            // Verify if it's being created a manager or an employee
            if (person.role != EMPLOYEE && person.role != MANAGER)
                throw ForbiddenException(ACCESS_DENIED, MANAGER_CREATE_PERSON)
        }

        private fun checkIfAllUpdatableParametersAreNull(person: UpdatePersonEntity) {
            if (person.name == null && person.phone == null && person.email == null && person.password == null)
                throw InvalidParameterException(
                    UPDATE_NULL_PARAMS,
                    detail = UPDATE_NULL_PARAMS_DETAIL,
                )
        }

        fun verifyUpdatePersonInput(person: UpdatePersonEntity): Boolean {
            checkIfAllUpdatableParametersAreNull(person)
            return true
        }

        fun personIsBanned(person: PersonDto): Boolean {
            return person.state.compareTo("banned") == 0
        }

        fun personIsInactive(person: PersonDto): Boolean {
            return person.state.compareTo("inactive") == 0
        }

        fun personIsGuest(roles: List<String>): Boolean {
            return roles.contains(GUEST)
        }
        fun personIsUser(roles: List<String>): Boolean {
            return roles.contains(USER)
        }
        fun personIsEmployee(roles: List<String>): Boolean {
            return roles.contains(EMPLOYEE)
        }
        fun personIsManager(roles: List<String>): Boolean {
            return roles.contains(MANAGER)
        }
        fun personIsAdmin(roles: List<String>): Boolean {
            return roles.contains(ADMIN)
        }

        fun personHasTwoRoles(roles: List<String>): Boolean {
            return roles.size >= 2
        }

        fun employeeHasTwoSkills(skills: List<String>): Boolean {
            return skills.size >= 2
        }

        fun isSamePerson(user: AuthPerson, reqPersonId: UUID) = user.id == reqPersonId

        fun belongsToCompany(user: AuthPerson, companyId: Long) = user.companies?.firstOrNull {
            it["id"].toString().toLong() == companyId
        } != null

        fun isBuildingManager(user: AuthPerson, companyId: Long, buildingId: Long) =
            user.companies?.firstOrNull {
                val list = it["manages"]?.serializeToJson()?.deserializeJsonTo<ArrayList<Int>>()
                it["id"].toString().toLong() == companyId
                        && list?.firstOrNull { e -> e.compareTo(buildingId) == 0 } != null
            } != null

        fun isEmployeeTicket(user: AuthPerson, ticketEmployeeId: UUID) = user.id == ticketEmployeeId

        fun verifyAddRoleInput(input: AddRoleToPersonEntity): Boolean {
            if ((input.role == EMPLOYEE || input.role == MANAGER) && input.company == null)
                throw InvalidParameterException(
                    ADD_EMPLOYEE_MANAGER_ROLE_WITHOUT_COMPANY,
                    listOf(InvalidParameter(COMPANY, Locations.BODY, NULL_EMPLOYEE_MANAGER_COMPANY))
                )
            if (input.role == EMPLOYEE && input.skill == null)
                throw InvalidParameterException(
                    CREATE_EMPLOYEE_WITHOUT_SKILL,
                    listOf(InvalidParameter(SKILL, Locations.BODY, EMPLOYEE_NULL_SKILL))
                )
            return true
        }
    }

    object AccessWithoutAuth {
        fun isAuthURI(requestURI: String): Boolean {
            return requestURI.compareTo(SIGNUP_PATH) == 0 || requestURI.compareTo(LOGIN_PATH) == 0
        }

        fun isReportURI(requestURI: String): Boolean {
            val req = requestURI.substringBeforeLast("/").plus("/{hash}")
            return req.compareTo(REPORT_PATH) == 0
        }

        fun isCreateTicketURI(requestURI: String, method: String): Boolean {
            return requestURI.compareTo(Uris.Tickets.BASE_PATH) == 0 && method == HttpMethod.POST.name
        }

        fun isCreatePersonURI(requestURI: String, method: String): Boolean {
            return requestURI.compareTo(Uris.Persons.BASE_PATH) == 0 && method == HttpMethod.POST.name
        }
    }

    object Auth {
        object Signup {
            private fun confirmPassword(password: String, confirmPassword: String) = password.compareTo(confirmPassword) == 0
            fun verifySignupInput(signupDto: SignupDto): Boolean {
                if (!confirmPassword(signupDto.password, signupDto.confirmPassword))
                    throw InvalidParameterException(
                        PASSWORD_MISMATCH_TITLE,
                        listOf(
                            InvalidParameter(
                                "$SIGNUP_PASSWORD/${SIGNUP_CONFIRM_PASSWORD}",
                                Locations.BODY,
                                PASSWORD_MISMATCH_REASON
                            )
                        )
                    )
                return true
            }
        }

        object Roles {
            fun isGuest(user: AuthPerson) = user.activeRole.compareTo(GUEST) == 0
            fun isUser(user: AuthPerson) = user.activeRole.compareTo(USER) == 0
            fun isEmployee(user: AuthPerson) = user.activeRole.compareTo(EMPLOYEE) == 0
            fun isManager(user: AuthPerson) = user.activeRole.compareTo(MANAGER) == 0
            fun isAdmin(user: AuthPerson) = user.activeRole.compareTo(ADMIN) == 0
        }

        object States {
            fun isInactive(state: String) = state.compareTo(INACTIVE) == 0
            fun isActive(state: String) = state.compareTo(ACTIVE) == 0
            fun isBanned(state: String) = state.compareTo(BANNED) == 0
        }
    }
}