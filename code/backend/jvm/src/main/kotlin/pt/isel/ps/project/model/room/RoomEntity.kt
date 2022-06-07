package pt.isel.ps.project.model.room

import java.util.*

/*
 * Name of the room representation output parameter
 */
const val ROOM_REP = "roomRep"

object RoomEntity {
    const val ROOM_NAME = "name"
    const val ROOM_FLOOR = "floor"
    const val ROOM_NAME_MAX_CHARS = 50
    const val MAX_FLOOR = 500
    const val MIN_FLOOR = -100
}

data class CreateRoomEntity(
    val name: String,
    val floor: Int
)

data class UpdateRoomEntity(
    val name: String
)

data class AddDeviceEntity(
    val deviceId: Long
)