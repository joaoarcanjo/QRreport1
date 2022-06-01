package pt.isel.ps.project.model.building

import java.util.*

/*
 * Name of the building representation output parameter
 */
const val BUILDING_REP = "buildingRep"

object BuildingEntity {
    const val BUILDING_NAME = "name"
    const val BUILDING_FLOORS = "floors"
    const val BUILDING_NAME_MAX_CHARS = 50
}

data class CreateBuildingEntity(
    val name: String,
    val floors: Int,
    val manager: UUID
)

data class UpdateBuildingEntity(
    val name: String?,
    val floors: Int?
)

data class ChangeManagerEntity(
    val manager: UUID
)