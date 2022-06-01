package pt.isel.ps.project.util

import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.Errors.BadRequest.Message.BLANK_PARAMS
import pt.isel.ps.project.exception.Errors.BadRequest.Message.BLANK_PARAMS_DETAIL
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_NAME_LENGTH
import pt.isel.ps.project.exception.Errors.BadRequest.Message.Company.INVALID_NAME_LENGTH
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
import pt.isel.ps.project.model.building.BuildingEntity.BUILDING_NAME
import pt.isel.ps.project.model.building.BuildingEntity.BUILDING_NAME_MAX_CHARS
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.comment.CommentEntity.COMMENT
import pt.isel.ps.project.model.comment.CommentEntity.COMMENT_MAX_CHARS
import pt.isel.ps.project.model.comment.InputCommentEntity
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.CompanyEntity.COMPANY_NAME
import pt.isel.ps.project.model.company.CompanyEntity.COMPANY_NAME_MAX_CHARS
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_SUBJECT
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_DESCRIPTION
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_SUBJECT_MAX_CHARS
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_DESCRIPTION_MAX_CHARS
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.model.ticket.CreateTicketEntity
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_HASH
import pt.isel.ps.project.model.ticket.TicketEntity.TICKET_RATE
import pt.isel.ps.project.model.ticket.TicketRateEntity
import pt.isel.ps.project.model.ticket.UpdateTicketEntity

object Validator {

    private fun checkIfIsNotBlank(input: String, parameterName: String) {
        if (input.isBlank()) {
            throw InvalidParameterException(
                BLANK_PARAMS_DETAIL,
                listOf(InvalidParameter(parameterName, Errors.BadRequest.Locations.BODY, BLANK_PARAMS))
            )
        }
    }

    object Company {

        private fun checkNameLength(name: String) {
            if (name.length > COMPANY_NAME_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(COMPANY_NAME, Errors.BadRequest.Locations.BODY, INVALID_NAME_LENGTH))
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
                    listOf(InvalidParameter(BUILDING_NAME, Errors.BadRequest.Locations.BODY, INVALID_BUILDING_NAME_LENGTH))
                )
            }

            /*
             * Verify if parameters to create building are valid.
             */
            fun verifyCreateBuildingInput(building: CreateBuildingEntity): Boolean {
                checkIfIsNotBlank(building.name, BUILDING_NAME)
                checkNameLength(building.name)
                return true
            }

            /*
             * Verify if parameters to create building are valid.
             */
            fun verifyUpdateBuildingInput(building: UpdateBuildingEntity): Boolean {
                building.name?.let {  checkIfIsNotBlank(it, BUILDING_NAME); checkNameLength(it) }
                return true
            }
        }
    }

    object Ticket {

        private fun checkSubjectLength(subject: String) {
            if (subject.length > TICKET_SUBJECT_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_SUBJECT, Errors.BadRequest.Locations.BODY, INVALID_SUBJECT_LENGTH))
            )
        }

        private fun checkDescriptionLength(description: String) {
            if (description.length > TICKET_DESCRIPTION_MAX_CHARS) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_DESCRIPTION, Errors.BadRequest.Locations.BODY, INVALID_DESCRIPTION_LENGTH))
            )
        }

        private fun checkHashLength(hash: String) {
            if (hash.length != Hash.SHA256.HASH_SIZE) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_HASH, Errors.BadRequest.Locations.BODY, INVALID_HASH_LENGTH))
            )
        }

        private fun checkRate(rate: Int) {
            if (rate !in 0..5) throw InvalidParameterException(
                INVALID_REQ_PARAMS,
                listOf(InvalidParameter(TICKET_RATE, Errors.BadRequest.Locations.BODY, INVALID_RATE))
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
                    listOf(InvalidParameter(COMMENT, Errors.BadRequest.Locations.BODY, INVALID_COMMENT_LENGTH))
                )
            }

            /*
             * Verify if the comment inserted is valid.
             */
            fun verifyCommentInput(comment: InputCommentEntity): Boolean {
                checkIfIsNotBlank(comment.comment, COMMENT)
                checkCommentLength(comment.comment)
                return true
            }
        }
    }
}