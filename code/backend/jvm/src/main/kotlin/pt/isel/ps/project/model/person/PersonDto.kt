package pt.isel.ps.project.model.person

import java.util.UUID

data class PersonItemDto (
    val id: UUID,
    val name: String,
    val phone: String?,
    val email: String
)