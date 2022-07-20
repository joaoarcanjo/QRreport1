package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.TicketDao
import pt.isel.ps.project.exception.Errors.BadRequest.Message.SAME_TICKET
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.responses.PersonResponses.PERSON_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.TicketResponses.TICKET_PAGE_MAX_SIZE
import pt.isel.ps.project.util.Validator.Ticket.verifyCreateTicketInput
import pt.isel.ps.project.util.Validator.Ticket.verifyTicketRateInput
import pt.isel.ps.project.util.Validator.Ticket.verifyUpdateTicketInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class TicketService(val ticketDao: TicketDao) {

    fun getTickets(user: AuthPerson, direction: String, sortBy: String, page: Int): TicketsDto {
        return ticketDao.getTickets(user.id, direction, sortBy, elemsToSkip(page, TICKET_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun getTicket(ticketId: Long, user: AuthPerson): TicketExtraInfo {
        return ticketDao.getTicket(ticketId, user.id).deserializeJsonTo()
    }

    fun createTicket(ticket: CreateTicketEntity): TicketItemDto {
        verifyCreateTicketInput(ticket)
        return ticketDao.createTicket(ticket).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun updateTicket(ticketId: Long, ticket: UpdateTicketEntity, user: AuthPerson): TicketItemDto {
        verifyUpdateTicketInput(ticket)
        return ticketDao.updateTicket(ticketId, user.id, ticket).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun changeTicketState(ticketId: Long, ticketState: ChangeTicketStateEntity, user: AuthPerson): TicketItemDto {
        return ticketDao.changeTicketState(ticketId, user.id, ticketState).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun addTicketRate(ticketId: Long, ticketRate: TicketRateEntity, user: AuthPerson): TicketRate {
        verifyTicketRateInput(ticketRate)
        return ticketDao.addTicketRate(ticketId, user.id, ticketRate).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getSpecificEmployees(ticketId: Long, user: AuthPerson, page: Int): PersonsDto {
        return ticketDao.getSpecificEmployees(ticketId, elemsToSkip(page, PERSON_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun setEmployee(ticketId: Long, ticketEmployee: TicketEmployeeEntity, user: AuthPerson): TicketEmployee {
        //verifyPersonId(ticketEmployee.employeeId) TODO
        return ticketDao.setEmployee(ticketId, user.id, ticketEmployee).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun removeEmployee(ticketId: Long, user: AuthPerson): TicketEmployee {
        return ticketDao.removeEmployee(ticketId, user.id).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun groupTicket(ticketId: Long, parentTicket: Long, user: AuthPerson): TicketItemDto {
        if (ticketId == parentTicket) throw InvalidParameterException(SAME_TICKET)
        return ticketDao.groupTicket(ticketId, parentTicket, user.id).getString(TICKET_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}