package pt.isel.ps.project.model.room

import java.sql.Timestamp

class RoomDto {
}

data class RoomItemDto (
    val id: Int,
    val name: String,
    val state: String
)