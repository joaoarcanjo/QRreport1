package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.responses.RoomResponses.ROOM_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.RoomResponses.activateRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.addDeviceToRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.createRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.deactivateRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.getRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.getRoomsRepresentation
import pt.isel.ps.project.responses.RoomResponses.removeDeviceFromRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.updateRoomRepresentation
import pt.isel.ps.project.service.RoomService

@RestController
class RoomController(private val service: RoomService) {

    @GetMapping(Uris.Companies.Buildings.Rooms.BASE_PATH)
    fun getRooms(@PathVariable companyId: Long, @PathVariable buildingId: Long): QRreportJsonModel {
        val rooms = service.getRooms(companyId, buildingId)
        return getRoomsRepresentation(
            rooms,
            companyId,
            buildingId,
            CollectionModel(1, ROOM_PAGE_MAX_SIZE, rooms.roomsCollectionSize),
            null
        )
    }

    @PostMapping(Uris.Companies.Buildings.Rooms.BASE_PATH)
    fun createRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody room: CreateRoomEntity
    ): ResponseEntity<QRreportJsonModel> {
        return createRoomRepresentation(service.createRoom(companyId, buildingId, room))
    }

    @GetMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_PATH)
    fun getRoom(@PathVariable roomId: Long): ResponseEntity<QRreportJsonModel> {
        return getRoomRepresentation(service.getRoom(roomId))
    }

    @PutMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_PATH)
    fun updateRoom(@PathVariable roomId: Long, @RequestBody room: UpdateRoomEntity): ResponseEntity<QRreportJsonModel> {
        return updateRoomRepresentation(service.updateRoom(roomId, room))
    }

    @PostMapping(Uris.Companies.Buildings.Rooms.DEVICES_PATH)
    fun addDeviceRoom(@PathVariable roomId: Long, @RequestBody device: AddDeviceEntity): ResponseEntity<QRreportJsonModel> {
        return addDeviceToRoomRepresentation(roomId, service.addRoomDevice(roomId, device))
    }

    @DeleteMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_DEVICE_PATH)
    fun removeDeviceRoom(@PathVariable roomId: Long, @PathVariable deviceId: Long): ResponseEntity<QRreportJsonModel> {
        return removeDeviceFromRoomRepresentation(roomId, service.removeRoomDevice(roomId, deviceId))
    }

    @DeleteMapping(Uris.Companies.Buildings.Rooms.SPECIFIC_PATH)
    fun deactivateRoom(@PathVariable roomId: Long): ResponseEntity<QRreportJsonModel> {
        return deactivateRoomRepresentation(service.deactivateRoom(roomId))
    }

    @PutMapping(Uris.Companies.Buildings.Rooms.ACTIVATE_PATH)
    fun activateRoom(@PathVariable roomId: Long): ResponseEntity<QRreportJsonModel> {
        return activateRoomRepresentation(service.activateRoom(roomId))
    }
}