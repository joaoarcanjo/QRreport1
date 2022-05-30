package pt.isel.ps.project.service

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.TicketDao
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.util.Validator.Ticket.verifyCreateTicketInput
import pt.isel.ps.project.util.Validator.Ticket.verifyTicketRateInput
import pt.isel.ps.project.util.Validator.Ticket.verifyUpdateTicketInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class TicketService(jdbi: Jdbi) {

    private val ticketDao = jdbi.onDemand<TicketDao>()

    fun getTickets(): TicketsDto {
        return ticketDao.getTickets().deserializeJsonTo()
    }

    fun createTicket(ticket: CreateTicketEntity): TicketItemDto {
        verifyCreateTicketInput(ticket)
        return ticketDao.createTicket(ticket).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getTicket(ticketId: Long): TicketExtraInfo {
        return ticketDao.getTicket(ticketId).deserializeJsonTo()
    }

    fun deleteTicket(ticketId: Long): TicketItemDto {
        return ticketDao.deleteTicket(ticketId).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun changeTicketState(ticketId: Long, ticketState: ChangeTicketStateEntity): TicketItemDto {
        return ticketDao.changeTicketState(ticketId, ticketState).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun updateTicket(ticketId: Long, ticket: UpdateTicketEntity): TicketItemDto {
        verifyUpdateTicketInput(ticket)
        return ticketDao.updateTicket(ticketId, ticket).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun addTicketRate(ticketId: Long, ticketRate: TicketRateEntity): TicketRate {
        verifyTicketRateInput(ticketRate)
        return ticketDao.addTicketRate(ticketId, ticketRate).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun setEmployee(ticketId: Long, ticketEmployee: TicketEmployeeEntity): TicketEmployee {
        //verifyPersonId(ticketEmployee.employeeId) TODO
        return ticketDao.setEmployee(ticketId, ticketEmployee).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun removeEmployee(ticketId: Long): TicketEmployee {
        return ticketDao.removeEmployee(ticketId).getString(TICKET_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}