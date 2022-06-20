package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import pt.isel.ps.project.dao.RoomDao
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.util.Validator.Company.Building.Room.verifyCreateRoomInput
import pt.isel.ps.project.util.Validator.Company.Building.Room.verifyUpdateRoomInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class RoomService(val roomDao: RoomDao) {

    //@Transactional(isolation = Isolation.READ_COMMITTED)
    fun getRooms(companyId: Long, buildingId: Long): RoomsDto {
        return roomDao.getRooms(companyId, buildingId).deserializeJsonTo()
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun getRoom(roomId: Long): RoomDto {
        return roomDao.getRoom(roomId).deserializeJsonTo()
    }

    //@Transactional(isolation = Isolation.SERIALIZABLE)
    fun createRoom(companyId: Long, buildingId: Long, room: CreateRoomEntity) : RoomItemDto {
        verifyCreateRoomInput(room)
        return roomDao.createRoom(companyId, buildingId, room).getString(ROOM_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.SERIALIZABLE)
    fun updateRoom(roomId: Long, room: UpdateRoomEntity): RoomItemDto {
        verifyUpdateRoomInput(room)
        return roomDao.updateRoom(roomId, room).getString(ROOM_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun addRoomDevice(roomId: Long, device: AddDeviceEntity): RoomDeviceDto {
        return roomDao.addRoomDevice(roomId, device).getString(ROOM_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.READ_COMMITTED)
    fun removeRoomDevice(roomId: Long, deviceId: Long): RoomDeviceDto {
        return roomDao.removeRoomDevice(roomId, deviceId).getString(ROOM_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun activateRoom(roomId: Long): RoomItemDto {
        return roomDao.activateRoom(roomId).getString(ROOM_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun deactivateRoom(roomId: Long): RoomDeactivateDto {
        return roomDao.deactivateRoom(roomId).getString(ROOM_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }
}