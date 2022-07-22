package pt.isel.ps.project.model.ticket

import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.company.CompanyItemDto
import pt.isel.ps.project.model.device.DeviceItemDto
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.room.RoomItemDto
import pt.isel.ps.project.model.state.EmployeeStateDto
import java.sql.Timestamp


data class TicketItemDto (
    val id: Long,
    val subject: String,
    val description: String?,
    val company: String,
    val building: String,
    val room: String,
    val userState: String?,
    val employeeState: String?,
)

data class TicketDto (
    val id: Long,
    val subject: String,
    val description: String,
    val creationTimestamp: Timestamp,
    val employeeState: String,
    val userState: String,
    val possibleTransitions: List<EmployeeStateDto>?,
)

data class TicketsDto (
    val tickets: List<TicketItemDto>?,
    val ticketsCollectionSize: Int,
)

data class TicketExtraInfo (
    val ticket: TicketDto,
    val ticketComments: CommentsDto,
    val person: PersonItemDto,
    val company: CompanyItemDto,
    val building: BuildingItemDto,
    val room: RoomItemDto,
    val device: DeviceItemDto,
    val employee: PersonItemDto?,
    val parentTicket: Long?,
)

data class TicketEmployee (
    val ticket: TicketItemDto,
    val person: PersonItemDto
)

//Includes the ticket rate
data class TicketRate (
    val id: Long,
    val subject: String,
    val description: String,
    val userState: String,
    val employeeState: String,
    val rate: Int
)

data class EmployeeState (
    val id: Int,
    val name: String
)

data class EmployeeStatesDto (
    val employeeStates: List<EmployeeState>?,
    val statesCollectionSize: Int,
)