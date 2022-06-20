package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.DeviceDao
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.comment.COMMENT_REP
import pt.isel.ps.project.model.device.*
import pt.isel.ps.project.model.room.RoomDeviceDto
import pt.isel.ps.project.util.Validator
import pt.isel.ps.project.util.Validator.Device.verifyCreateDeviceInput
import pt.isel.ps.project.util.Validator.Device.verifyUpdateDeviceInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class DeviceService(private val deviceDao: DeviceDao) {

    fun getDevices(): DevicesDto {
        return deviceDao.getDevices().deserializeJsonTo()
    }

    fun getDevice(deviceId: Long): DeviceDto {
        return deviceDao.getDevice(deviceId).deserializeJsonTo()
    }

    fun createDevice(device: CreateDeviceEntity): DeviceItemDto {
        verifyCreateDeviceInput(device)
        return deviceDao.createDevice(device).getString(DEVICE_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun updateDevice(deviceId: Long, device: UpdateDeviceEntity): DeviceItemDto {
        verifyUpdateDeviceInput(device)
        return deviceDao.updateDevice(deviceId, device).getString(DEVICE_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun changeCategoryDevice(deviceId: Long, category: ChangeDeviceCategoryEntity): DeviceItemDto {
        return deviceDao.changeDeviceCategory(deviceId, category).getString(DEVICE_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun deactivateDevice(deviceId: Long): DeviceItemDto {
        return deviceDao.deactivateDevice(deviceId).getString(DEVICE_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun activateDevice(deviceId: Long): DeviceItemDto {
        return deviceDao.activateDevice(deviceId).getString(DEVICE_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun getRoomDevices(roomId: Long): DevicesDto {
        return deviceDao.getRoomDevices(roomId).deserializeJsonTo()
    }

    fun getRoomDevice(roomId: Long, deviceId: Long): DeviceQrCodeDto {
        return deviceDao.getRoomDevice(roomId, deviceId).deserializeJsonTo()
    }
}