package pt.isel.ps.project.model.ticket

import java.util.UUID

const val TICKET_REP = "ticket_rep"

object TicketEntity {
    const val TICKET_HASH = "hash"
    const val TICKET_SUBJECT = "subject"
    const val TICKET_DESCRIPTION = "description"
    const val TICKET_SUBJECT_MAX_CHARS = 50
    const val TICKET_DESCRIPTION_MAX_CHARS = 200
}

data class CreateTicketEntity(
    val subject: String,
    val description: String,
    val hash: String,
)

data class ChangeTicketStateEntity (
    val newStateId: Int
)

data class UpdateTicketEntity (
    val subject: String?,
    val description: String?,
)

data class TicketRateValueEntity (
    val rate: Int
)

data class TicketEmployeeEntity (
    val employeeId: UUID
)