package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Tickets
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.service.TicketService

@RestController
class TicketController(private val service: TicketService) {

    @GetMapping(Tickets.BASE_PATH)
    fun getTickets(): TicketsDto {
        return service.getTickets()
    }

    @GetMapping(Tickets.SPECIFIC_PATH)
    fun getTicket(@PathVariable ticketId: Long): TicketExtraInfo {
        return service.getTicket(ticketId)
    }

    @PostMapping(Tickets.BASE_PATH)
    fun createTicket(@RequestBody ticket: CreateTicketEntity): TicketItemDto {
        return service.createTicket(ticket)
    }

    @PutMapping(Tickets.SPECIFIC_PATH)
    fun updateTicket(@PathVariable ticketId: Long, @RequestBody ticket: UpdateTicketEntity): TicketItemDto {
        return service.updateTicket(ticketId, ticket)
    }

    @DeleteMapping(Tickets.SPECIFIC_PATH)
    fun deleteTicket(@PathVariable ticketId: Long): TicketItemDto {
        return service.deleteTicket(ticketId)
    }

    @PutMapping(Tickets.STATE_PATH)
    fun changeTicketState(@PathVariable ticketId: Long, @RequestBody ticketState: ChangeTicketStateEntity): TicketItemDto {
        return service.changeTicketState(ticketId, ticketState)
    }

    @PutMapping(Tickets.RATE_PATH)
    fun addTicketRate(@PathVariable ticketId: Long, @RequestBody ticketRate: TicketRateEntity): TicketRate {
        return service.addTicketRate(ticketId, ticketRate)
    }

    @PostMapping(Tickets.EMPLOYEE_PATH)
    fun setEmployee(@PathVariable ticketId: Long, @RequestBody ticketEmployee: TicketEmployeeEntity): TicketEmployee {
        return service.setEmployee(ticketId, ticketEmployee)
    }

    @DeleteMapping(Tickets.EMPLOYEE_PATH)
    fun removeEmployee(@PathVariable ticketId: Long): TicketEmployee {
        return service.removeEmployee(ticketId)
    }
}