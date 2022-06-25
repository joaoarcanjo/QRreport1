package pt.isel.ps.project.util

import pt.isel.ps.project.exception.Errors.BadRequest.Locations
import pt.isel.ps.project.exception.Errors.BadRequest.Message.BLANK_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.BLANK_PARAMS_DETAIL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.CREATE_EMPLOYEE_WITH_SKILL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_FLOOR_NUMBER
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_FLOOR_NUMBER
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Category.INVALID_CATEGORY_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.INVALID_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Device.Anomaly.INVALID_ANOMALY_ANOMALY_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Device.INVALID_DEVICE_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.EMPLOYEE_NULL_SKILL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.INVALID_REQ_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.Comment.INVALID_COMMENT_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_DESCRIPTION_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_HASH_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_RATE
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Ticket.INVALID_SUBJECT_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.UPDATE_NULL_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.UPDATE_NULL_PARAMS_DETAIL
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
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
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.CompanyEntity.COMPANY_NAME
import pt.isel.ps.project.model.company.CompanyEntity.COMPANY_NAME_MAX_CHARS
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_SUBJECT
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_DESCRIPTION
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_SUBJECT_MAX_CHARS
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_DESCRIPTION_MAX_CHARS
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.DeviceEntity.DEVICE_NAME
import pt.isel.ps.project.model.device.DeviceEntity.DEVICE_NAME_MAX_CHARS
import pt.isel.ps.project.model.device.UpdateDeviceEntity
import pt.isel.ps.project.model.person.CreatePersonEntity
import pt.isel.ps.project.model.person.PersonDetailsDto
import pt.isel.ps.project.model.person.PersonDto
import pt.isel.ps.project.model.person.PersonEntity.SKILL
import pt.isel.ps.project.model.person.UpdatePersonEntity
import pt.isel.ps.project.model.room.CreateRoomEntity
import pt.isel.ps.project.model.room.RoomEntity.MAX_FLOOR
import pt.isel.ps.project.model.room.RoomEntity.MIN_FLOOR
import pt.isel.ps.project.model.room.RoomEntity.ROOM_FLOOR
import pt.isel.ps.project.model.room.RoomEntity.ROOM_NAME
import pt.isel.ps.project.model.room.RoomEntity.ROOM_NAME_MAX_CHARS
import pt.isel.ps.project.model.room.UpdateRoomEntity
import pt.isel.ps.project.model.ticket.CreateTicketEntity
import pt.isel.ps.project.model.ticket.TicketEntity.MAX_RATE
import pt.isel.ps.project.model.ticket.TicketEntity.MIN_RATE
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_HASH
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_RATE
import pt.isel.ps.project.model.ticket.TicketRateEntity
import pt.isel.ps.project.model.ticket.UpdateTicketEntity

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

        object Person {
            fun verifyCreatePersonInput(person: CreatePersonEntity): Boolean {
                // TODO: Verify if logged user can add person (manager and admin only)
                // TODO: Managers can only add employees

                // Employees must be linked to a skill
                if (person.role == "employee" && person.skill == null) {
                    throw InvalidParameterException(
                        CREATE_EMPLOYEE_WITH_SKILL,
                        listOf(InvalidParameter(SKILL, Locations.BODY, EMPLOYEE_NULL_SKILL))
                    )
                }
                return true
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
                return roles.contains("guest")
            }
            fun personIsUser(roles: List<String>): Boolean {
                return roles.contains("user")
            }
            fun personIsEmployee(roles: List<String>): Boolean {
                return roles.contains("employee")
            }
            fun personIsManager(roles: List<String>): Boolean {
                return roles.contains("manager")
            }
            fun personIsAdmin(roles: List<String>): Boolean {
                return roles.contains("admin")
            }

            fun personHasTwoRoles(roles: List<String>): Boolean {
                return roles.size >= 2
            }
        }
    }
}