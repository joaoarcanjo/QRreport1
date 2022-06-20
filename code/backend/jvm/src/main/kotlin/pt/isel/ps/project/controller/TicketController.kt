package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Tickets
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.responses.TicketResponses.TICKET_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.TicketResponses.addTicketRateRepresentation
import pt.isel.ps.project.responses.TicketResponses.changeTicketStateRepresentation
import pt.isel.ps.project.responses.TicketResponses.createTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.deleteTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.getTicketRepresentation
import pt.isel.ps.project.responses.TicketResponses.getTicketsRepresentation
import pt.isel.ps.project.responses.TicketResponses.removeEmployeeRepresentation
import pt.isel.ps.project.responses.TicketResponses.setEmployeeRepresentation
import pt.isel.ps.project.responses.TicketResponses.updateTicketRepresentation
import pt.isel.ps.project.service.TicketService

@RestController
class TicketController(private val service: TicketService) {

    @GetMapping(Tickets.BASE_PATH)
    fun getTickets(): QRreportJsonModel {
        val tickets = service.getTickets()
        return getTicketsRepresentation(
            tickets, CollectionModel(1, TICKET_PAGE_MAX_SIZE, tickets.ticketsCollectionSize)
        )
    }

    @GetMapping(Tickets.SPECIFIC_PATH)
    fun getTicket(@PathVariable ticketId: Long): QRreportJsonModel {
        return getTicketRepresentation(service.getTicket(ticketId))
    }

    @PostMapping(Tickets.BASE_PATH)
    fun createTicket(@RequestBody ticket: CreateTicketEntity): ResponseEntity<QRreportJsonModel> {
        return createTicketRepresentation(service.createTicket(ticket))
    }

    @PutMapping(Tickets.SPECIFIC_PATH)
    fun updateTicket(
        @PathVariable ticketId: Long,
        @RequestBody ticket: UpdateTicketEntity
    ): ResponseEntity<QRreportJsonModel> {
        return updateTicketRepresentation(service.updateTicket(ticketId, ticket))
    }

    @DeleteMapping(Tickets.SPECIFIC_PATH)
    fun deleteTicket(@PathVariable ticketId: Long): ResponseEntity<QRreportJsonModel> {
        return deleteTicketRepresentation(service.deleteTicket(ticketId))
    }

    @PutMapping(Tickets.STATE_PATH)
    fun changeTicketState(
        @PathVariable ticketId: Long,
        @RequestBody ticketState: ChangeTicketStateEntity
    ): ResponseEntity<QRreportJsonModel> {
        return changeTicketStateRepresentation(service.changeTicketState(ticketId, ticketState))
    }

    @PutMapping(Tickets.RATE_PATH)
    fun addTicketRate(
        @PathVariable ticketId: Long,
        @RequestBody ticketRate: TicketRateEntity
    ): ResponseEntity<QRreportJsonModel> {
        return addTicketRateRepresentation(service.addTicketRate(ticketId, ticketRate))
    }

    @PostMapping(Tickets.EMPLOYEE_PATH)
    fun setEmployee(
        @PathVariable ticketId: Long,
        @RequestBody ticketEmployee: TicketEmployeeEntity
    ): ResponseEntity<QRreportJsonModel> {
        return setEmployeeRepresentation(service.setEmployee(ticketId, ticketEmployee))
    }

    @DeleteMapping(Tickets.EMPLOYEE_PATH)
    fun removeEmployee(@PathVariable ticketId: Long): QRreportJsonModel {
        return removeEmployeeRepresentation(service.removeEmployee(ticketId))
    }
}