package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Room.activateRoomAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.addRoomDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.createRoomAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.deactivateRoomAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.getRoomAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.getRoomsAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.removeRoomDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Room.updatRoomAuthorization
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.responses.RoomResponses.ROOM_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.RoomResponses.addDeviceToRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.createRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.deactivateActivateRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.getRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.getRoomsRepresentation
import pt.isel.ps.project.responses.RoomResponses.removeDeviceFromRoomRepresentation
import pt.isel.ps.project.responses.RoomResponses.updateRoomRepresentation
import pt.isel.ps.project.service.RoomService

@RestController
class RoomController(private val service: RoomService) {

    @GetMapping(Rooms.BASE_PATH)
    fun getRooms(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        user: AuthPerson,
    ): QRreportJsonModel {
        getRoomsAuthorization(user)
        val rooms = service.getRooms(user, companyId, buildingId, page)
        return getRoomsRepresentation(
            user,
            companyId,
            buildingId,
            rooms,
            CollectionModel(page, ROOM_PAGE_MAX_SIZE, rooms.roomsCollectionSize),
            null
        )
    }

    @PostMapping(Rooms.BASE_PATH)
    fun createRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody room: CreateRoomEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        createRoomAuthorization(user)
        return createRoomRepresentation(companyId, buildingId, service.createRoom(user, companyId, buildingId, room))
    }

    @GetMapping(Rooms.SPECIFIC_PATH)
    fun getRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        getRoomAuthorization(user)
        return getRoomRepresentation(
            user,
            companyId,
            buildingId,
            service.getRoom(user, companyId, buildingId, roomId))
    }

    @PutMapping(Rooms.SPECIFIC_PATH)
    fun updateRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        @RequestBody room: UpdateRoomEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updatRoomAuthorization(user)
        return updateRoomRepresentation(companyId, buildingId, service.updateRoom(user, companyId, buildingId, roomId, room))
    }

    @PostMapping(Rooms.DEACTIVATE_PATH)
    fun deactivateRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        deactivateRoomAuthorization(user)
        return deactivateActivateRoomRepresentation(companyId, buildingId, service.deactivateRoom(user, companyId, buildingId, roomId))
    }

    @PostMapping(Rooms.ACTIVATE_PATH)
    fun activateRoom(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        activateRoomAuthorization(user)
        return deactivateActivateRoomRepresentation(companyId, buildingId, service.activateRoom(user, companyId, buildingId, roomId))
    }

    @PostMapping(Rooms.DEVICES_PATH)
    fun addRoomDevice(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        @RequestBody device: AddDeviceEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        addRoomDeviceAuthorization(user)
        return addDeviceToRoomRepresentation(companyId, buildingId, roomId, service.addRoomDevice(user, companyId, buildingId, roomId, device))
    }

    @DeleteMapping(Rooms.SPECIFIC_DEVICE_PATH)
    fun removeRoomDevice(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        @PathVariable deviceId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        removeRoomDeviceAuthorization(user)
        return removeDeviceFromRoomRepresentation(companyId, buildingId, roomId, service.removeRoomDevice(user, companyId, buildingId, roomId, deviceId))
    }
}