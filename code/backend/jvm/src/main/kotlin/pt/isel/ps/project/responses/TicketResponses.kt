package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.responses.BuildingResponses.getBuildingItem
import pt.isel.ps.project.responses.CommentResponses.COMMENT_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.CommentResponses.getCommentsRepresentation
import pt.isel.ps.project.responses.CompanyResponses.getCompanyItem
import pt.isel.ps.project.responses.DeviceResponses.getDeviceItem
import pt.isel.ps.project.responses.PersonResponses.getPersonItem
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader
import pt.isel.ps.project.responses.RoomResponses.getRoomItem

object TicketResponses {
    const val TICKET_PAGE_MAX_SIZE = 10

    object Actions {
        fun deleteTicket(ticketId: Long) = QRreportJsonModel.Action(
            name = "delete-ticket",
            title = "Delete ticket",
            method = HttpMethod.DELETE,
            href = Uris.Tickets.makeSpecific(ticketId)
        )

        fun updateTicket(ticketId: Long) = QRreportJsonModel.Action(
            name = "update-ticket",
            title = "Update ticket",
            method = HttpMethod.PUT,
            href = Uris.Tickets.makeSpecific(ticketId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("subject", "string"),
                QRreportJsonModel.Property("description", "string")
            )
        )
        fun changeTicketState(ticketId: Long) = QRreportJsonModel.Action(
            name = "update-ticket-state",
            title = "Update ticket state",
            method = HttpMethod.PUT,
            href = Uris.Tickets.makeState((ticketId)),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("state", "number"),
            )
        )

        fun setEmployee(ticketId: Long) = QRreportJsonModel.Action(
            name = "set-employee",
            title = "Set employee",
            method = HttpMethod.POST,
            href = Uris.Tickets.makeEmployee((ticketId)),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "employee", type = "string",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Persons.BASE_PATH)),
            )
        )

        fun removeEmployee(ticketId: Long) = QRreportJsonModel.Action(
            name = "remove-employee",
            title = "Remove employee",
            method = HttpMethod.DELETE,
            href = Uris.Tickets.makeEmployee((ticketId))
        )

        fun addRate(ticketId: Long) = QRreportJsonModel.Action(
            name = "add-rate",
            title = "Submit rating",
            method = HttpMethod.PUT,
            href = Uris.Tickets.makeRate((ticketId)),
            properties = listOf(
                QRreportJsonModel.Property("rate", "number"),
            )
        )
    }

    private fun getTicketItem(ticket: TicketItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.TICKET),
        rel = rel,
        properties = ticket,
        links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.id)))
    )

    fun getTicketsRepresentation(ticketsDto: TicketsDto, pageIdx: Int) = QRreportJsonModel(
        clazz = listOf(Classes.TICKET, Classes.COLLECTION),
        properties = CollectionModel(pageIdx, TICKET_PAGE_MAX_SIZE, ticketsDto.ticketsCollectionSize),
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (ticketsDto.tickets != null) addAll(ticketsDto.tickets.map { getTicketItem(it, listOf(Relations.ITEM)) })
        },
        links = listOf(Links.self(Uris.makePagination(pageIdx, Uris.Tickets.BASE_PATH))),
    )

    fun getTicketRepresentation(user: AuthPerson, ticketInfo: TicketExtraInfo)
            = QRreportJsonModel(
        clazz = listOf(Classes.TICKET),
        properties = ticketInfo.ticket,
        entities = mutableListOf<QRreportJsonModel>().apply {
            add(getCommentsRepresentation(
                user,
                ticketInfo.ticketComments,
                ticketInfo.ticket.id,
                ticketInfo.ticket.employeeState,
                CollectionModel(0, COMMENT_PAGE_MAX_SIZE, ticketInfo.ticketComments.collectionSize),
                listOf(Relations.TICKET_COMMENTS)))
            add(getCompanyItem(ticketInfo.company, listOf(Relations.TICKET_COMPANY)))
            add(getBuildingItem(ticketInfo.company.id, ticketInfo.building, listOf(Relations.TICKET_BUILDING)))
            add(getRoomItem(1, 1, ticketInfo.room, listOf(Relations.TICKET_ROOM))) // TODO
            add(getDeviceItem(ticketInfo.device, listOf(Relations.TICKET_DEVICE)))
            add(getPersonItem(ticketInfo.person, listOf(Relations.TICKET_AUTHOR)))
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (ticketInfo.ticket.employeeState.compareTo("Archived") == 0) return@apply
            add(Actions.deleteTicket(ticketInfo.ticket.id))
            if (ticketInfo.ticket.employeeState.compareTo("To assign") == 0)
                add(Actions.updateTicket(ticketInfo.ticket.id))
            add(Actions.changeTicketState(ticketInfo.ticket.id))
            add(Actions.setEmployee(ticketInfo.ticket.id))
            //add(Actions.removeEmployee(ticketInfo.ticket.id))
            add(Actions.addRate(ticketInfo.ticket.id))
        },
        links = listOf(Links.self(Uris.Tickets.makeSpecific(ticketInfo.ticket.id)), Links.tickets())
    )

    fun createTicketRepresentation(ticket: TicketItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.TICKET),
            properties = ticket,
            links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Tickets.makeSpecific(ticket.id)),
    )

    fun updateTicketRepresentation(ticket: TicketItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.TICKET),
            properties = ticket,
            links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.id)))
        )
    )

    fun deleteTicketRepresentation(ticket: TicketItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.TICKET),
            properties = ticket,
            links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.id)), Links.tickets())
        )
    )

    fun addTicketRateRepresentation(ticket: TicketRate) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.TICKET),
            properties = ticket,
            links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.id)))
        )
    )

    fun changeTicketStateRepresentation(ticket: TicketItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.TICKET),
            properties = ticket,
            links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.id)))
        )
    )

    fun setEmployeeRepresentation(ticket: TicketEmployee) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.TICKET),
            properties = ticket.ticket,
            entities = listOf(getPersonItem(ticket.person, listOf(Relations.TICKET_EMPLOYEE))),
            links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.ticket.id)))
        )
    )

    fun removeEmployeeRepresentation(ticket: TicketEmployee) = QRreportJsonModel(
        clazz = listOf(Classes.TICKET),
        properties = ticket.ticket,
        entities = listOf(getPersonItem(ticket.person, listOf(Relations.TICKET_EMPLOYEE))),
        links = listOf(Links.self(Uris.Tickets.makeSpecific(ticket.ticket.id)))
    )
}