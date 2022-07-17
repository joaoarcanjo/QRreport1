package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Ticket.addTicketRateAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.changeTicketStateAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.getTicketAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.getTicketsAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.groupTicketAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.refuseTicketAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.removeEmployeeAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.setEmployeeAuthorization
import pt.isel.ps.project.auth.Authorizations.Ticket.updateTicketAuthorization
import pt.isel.ps.project.model.Uris.Tickets
import pt.isel.ps.project.model.representations.DEFAULT_DIRECTION
import pt.isel.ps.project.model.representations.DEFAULT_SORT
import pt.isel.ps.project.model.representations.PaginationDto.Companion.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.responses.TicketResponses.addTicketRateRepresentation
import pt.isel.ps.project.responses.TicketResponses.changeTicketStateRepresentation
import pt.isel.ps.project.responses.TicketResponses.createTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.deleteTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.getTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.getTicketsRepresentation
import pt.isel.ps.project.responses.TicketResponses.groupTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.removeEmployeeRepresentation
import pt.isel.ps.project.responses.TicketResponses.setEmployeeRepresentation
import pt.isel.ps.project.responses.TicketResponses.updateTicketRepresentation
import pt.isel.ps.project.service.TicketService

@RestController
class TicketController(private val service: TicketService) {

    @GetMapping(Tickets.BASE_PATH)
    fun getTickets(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        @RequestParam(defaultValue = DEFAULT_DIRECTION) direction: String,
        @RequestParam(defaultValue = DEFAULT_SORT) sortBy: String,
        user: AuthPerson
    ): QRreportJsonModel {
        getTicketsAuthorization(user)
        return getTicketsRepresentation(service.getTickets(user, direction, sortBy, page), page)
    }

    @GetMapping(Tickets.SPECIFIC_PATH)
    fun getTicket(@PathVariable ticketId: Long, user: AuthPerson): QRreportJsonModel {
        getTicketAuthorization(user)
        return getTicketRepresentation(user, service.getTicket(ticketId, user))
    }

    @PostMapping(Tickets.BASE_PATH)
    fun createTicket(@RequestBody ticket: CreateTicketEntity): ResponseEntity<QRreportJsonModel> {
        return createTicketRepresentation(service.createTicket(ticket))
    }

    @PutMapping(Tickets.SPECIFIC_PATH)
    fun updateTicket(
        @PathVariable ticketId: Long,
        @RequestBody ticket: UpdateTicketEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updateTicketAuthorization(user)
        return updateTicketRepresentation(service.updateTicket(ticketId, ticket, user))
    }

    /*@DeleteMapping(Tickets.SPECIFIC_PATH) // TODO: Delete?
    fun refuseTicket(@PathVariable ticketId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        refuseTicketAuthorization(user)
        return deleteTicketRepresentation(service.refuseTicket(ticketId))
    }*/

    @PutMapping(Tickets.STATE_PATH)
    fun changeTicketState(
        @PathVariable ticketId: Long,
        @RequestBody ticketState: ChangeTicketStateEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        changeTicketStateAuthorization(user)
        return changeTicketStateRepresentation(service.changeTicketState(ticketId, ticketState, user))
    }

    @PutMapping(Tickets.RATE_PATH)
    fun addTicketRate(
        @PathVariable ticketId: Long,
        @RequestBody ticketRate: TicketRateEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        addTicketRateAuthorization(user)
        return addTicketRateRepresentation(service.addTicketRate(ticketId, ticketRate, user))
    }

    @PutMapping(Tickets.EMPLOYEE_PATH)
    fun setEmployee(
        @PathVariable ticketId: Long,
        @RequestBody ticketEmployee: TicketEmployeeEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        setEmployeeAuthorization(user)
        return setEmployeeRepresentation(service.setEmployee(ticketId, ticketEmployee, user))
    }

    @DeleteMapping(Tickets.EMPLOYEE_PATH)
    fun removeEmployee(@PathVariable ticketId: Long, user: AuthPerson): QRreportJsonModel {
        removeEmployeeAuthorization(user)
        return removeEmployeeRepresentation(service.removeEmployee(ticketId, user))
    }

    @PutMapping(Tickets.EMPLOYEE_PATH)
    fun groupTicket(
        @PathVariable ticketId: Long,
        @RequestBody parentTicket: ParentTicketEntity,
        user: AuthPerson,
    ): QRreportJsonModel {
        groupTicketAuthorization(user)
        return groupTicketRepresentation(service.groupTicket(ticketId, parentTicket.ticket, user))
    }
}