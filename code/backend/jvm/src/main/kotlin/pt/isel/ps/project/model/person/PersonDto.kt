package pt.isel.ps.project.model.person

import pt.isel.ps.project.model.ticket.TicketsDto
import java.sql.Timestamp
import java.util.UUID

data class PersonItemDto (
    val id: UUID,
    val name: String,
    val phone: String?,
    val email: String,
    val roles: List<String>,
    val skills: List<String>?,
    val state: String?,
)

data class PersonsDto(
    val persons: List<PersonItemDto>?,
    val personsCollectionSize: Int,
)

data class PersonDto (
    val id: UUID,
    val name: String,
    val phone: String?,
    val email: String,
    val roles: List<String>,
    val skills: List<String>?,
    val companies: List<String>?,
    val timestamp: Timestamp?,
    val state: String,
    val reason: String?,
    val bannedBy: PersonDto?,
)

data class PersonDetailsDto(
    val person: PersonDto,
    val personTickets: TicketsDto?,
)