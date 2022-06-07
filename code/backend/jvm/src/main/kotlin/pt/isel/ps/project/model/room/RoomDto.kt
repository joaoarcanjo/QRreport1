package pt.isel.ps.project.model.room

import pt.isel.ps.project.model.Device.DeviceItemDto
import java.sql.Timestamp

data class RoomItemDto (
    val id: Long,
    val name: String,
    val floor: Int,
    val state: String,
    val timestamp: Timestamp?,
)

data class RoomDto (
    val id: Long,
    val name: String,
    val floor: Int,
    val state: String,
    val timestamp: Timestamp,
    val devices: List<DeviceItemDto>?,
    val devicesCollectionSize: Int
)

data class RoomDeviceDto (
    val room: RoomItemDto,
    val device: DeviceItemDto
)

data class RoomsDto (
    val rooms: List<RoomItemDto>?,
    val roomsCollectionSize: Int,
)