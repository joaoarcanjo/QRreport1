package pt.isel.ps.project.model.person

import java.sql.Timestamp
import java.util.UUID

data class PersonItemDto (
    val id: UUID,
    val name: String,
    val phone: String?,
    val email: String,
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
    val state: String,
    val roles: List<String>,
    val timestamp: Timestamp,
)
