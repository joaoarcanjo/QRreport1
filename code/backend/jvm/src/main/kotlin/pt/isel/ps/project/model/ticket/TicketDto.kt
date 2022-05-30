package pt.isel.ps.project.model.ticket

import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.state.StateDto
import java.sql.Timestamp


data class TicketItemDto (
    val id: Int,
    val subject: String,
    val description: String,
    val userState: String,
    val employeeState: String
)

data class TicketDto (
    val id: Int,
    val subject: String,
    val description: String,
    val timestamp: Timestamp,
    val employeeState: String,
    val userState: String,
    val possibleTransitions: List<StateDto>
)

data class TicketsDto (
    val tickets: List<TicketItemDto>?,
    val collectionSize: Int,
)

data class TicketExtraInfo (
    val ticket: TicketDto,
    val ticketComments: CommentsDto,
    val reporter: PersonItemDto
)

data class TicketEmployee (
    val ticket: TicketItemDto,
    val person: PersonItemDto
)

//Includes the ticket rate
data class TicketRate (
    val id: Int,
    val subject: String,
    val description: String,
    val userState: String,
    val employeeState: String,
    val rate: Int
)
