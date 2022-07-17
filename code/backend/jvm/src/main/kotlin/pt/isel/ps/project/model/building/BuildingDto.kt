package pt.isel.ps.project.model.building

import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.room.RoomItemDto
import pt.isel.ps.project.model.room.RoomsDto
import java.sql.Timestamp
import java.util.*

data class BuildingItemDto (
    val id: Long,
    val name: String,
    val floors: Int,
    val state: String,
    val timestamp: Timestamp?
)

data class BuildingDto (
    val building: BuildingItemDto,
    val rooms: RoomsDto,
    val manager: PersonItemDto
)

data class BuildingManagerDto(
    val id: Long,
    val name: String,
    val manager: UUID
)

data class BuildingsDto (
    val buildings: List<BuildingItemDto>?,
    val buildingsCollectionSize: Int
)