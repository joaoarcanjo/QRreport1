package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.service.RoomService

@RestController
class RoomController(private val service: RoomService) {

    @GetMapping(Uris.Companies.Buildings.Rooms.BASE_PATH)
    fun getRooms(@PathVariable companyId: Long, @PathVariable buildingId: Long): RoomsDto {
        return service.getRooms(companyId, buildingId)
    }

    @PostMapping(Uris.Companies.Buildings.Rooms.BASE_PATH)
    fun createRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody room: CreateRoomEntity
    ): RoomItemDto {
        return service.createRoom(companyId, buildingId, room)
    }

    @GetMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_PATH)
    fun getRoom(@PathVariable roomId: Long): RoomDto {
        return service.getRoom(roomId)
    }

    @PutMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_PATH)
    fun updateRoom(@PathVariable roomId: Long, @RequestBody room: UpdateRoomEntity): RoomItemDto {
        return service.updateRoom(roomId, room)
    }

    @PostMapping(Uris.Companies.Buildings.Rooms.DEVICES_PATH)
    fun addDeviceRoom(@PathVariable roomId: Long, @RequestBody device: AddDeviceEntity): RoomDeviceDto {
        return service.addRoomDevice(roomId, device)
    }

    @DeleteMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_DEVICE_PATH)
    fun removeDeviceRoom(@PathVariable roomId: Long, @PathVariable deviceId: Long): RoomDeviceDto {
        return service.removeRoomDevice(roomId, deviceId)
    }

    @DeleteMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_PATH)
    fun deactivateRoom(@PathVariable roomId: Long): RoomItemDto {
        return service.deactivateRoom(roomId)
    }

    @PutMapping(Uris.Companies.Buildings.Rooms.ACTIVATE_PATH)
    fun activateRoom(@PathVariable roomId: Long): RoomItemDto {
        return service.activateRoom(roomId)
    }
}