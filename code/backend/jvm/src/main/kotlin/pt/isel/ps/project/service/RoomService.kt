package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.RoomDao
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CHANGE_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CREATION_DENIED
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.responses.RoomResponses.ROOM_PAGE_MAX_SIZE
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Company.Building.Room.verifyCreateRoomInput
import pt.isel.ps.project.util.Validator.Company.Building.Room.verifyUpdateRoomInput
import pt.isel.ps.project.util.Validator.Person.belongsToCompany
import pt.isel.ps.project.util.Validator.Person.isBuildingManager
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class RoomService(val roomDao: RoomDao) {

    fun getRooms(user: AuthPerson, companyId: Long, buildingId: Long, page: Int): RoomsDto {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return roomDao.getRooms(companyId, buildingId, elemsToSkip(page, ROOM_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun createRoom(user: AuthPerson, companyId: Long, buildingId: Long, room: CreateRoomEntity) : RoomItemDto {
        verifyCreateRoomInput(room)
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId))
            throw ForbiddenException(CREATION_DENIED)
        return roomDao.createRoom(companyId, buildingId, room).getString(ROOM_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getRoom(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long): RoomDto {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return roomDao.getRoom(companyId, buildingId, roomId).deserializeJsonTo()
    }

    fun updateRoom(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, room: UpdateRoomEntity): RoomItemDto {
        verifyUpdateRoomInput(room)
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId))
            throw ForbiddenException(CHANGE_DENIED)
        return roomDao.updateRoom(companyId, buildingId, roomId, room).getString(ROOM_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deactivateRoom(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long): RoomItemDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId))
            throw ForbiddenException(CHANGE_DENIED)
        return roomDao.deactivateRoom(companyId, buildingId, roomId).getString(ROOM_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun activateRoom(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long): RoomItemDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId))
            throw ForbiddenException(CHANGE_DENIED)
        return roomDao.activateRoom(companyId, buildingId, roomId).getString(ROOM_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun addRoomDevice(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, device: AddDeviceEntity): RoomDeviceDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId))
            throw ForbiddenException(CHANGE_DENIED)
        return roomDao.addRoomDevice(companyId, buildingId, roomId, device).getString(ROOM_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun removeRoomDevice(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): RoomDeviceDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId))
            throw ForbiddenException(CHANGE_DENIED)
        return roomDao.removeRoomDevice(companyId, buildingId, roomId, deviceId).getString(ROOM_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}