package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.DeviceDao
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.device.*
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.responses.DeviceResponses.DEVICES_PAGE_MAX_SIZE
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Device.verifyCreateDeviceInput
import pt.isel.ps.project.util.Validator.Device.verifyUpdateDeviceInput
import pt.isel.ps.project.util.Validator.Person.belongsToCompany
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class DeviceService(private val deviceDao: DeviceDao) {

    fun getDevices(page: Int): DevicesDto {
        return deviceDao.getDevices(elemsToSkip(page, DEVICES_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun getActiveDevices(page: Int): DevicesDto {
        return deviceDao.getDevices(elemsToSkip(page, DEVICES_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun getDevice(deviceId: Long): DeviceDto {
        return deviceDao.getDevice(deviceId).deserializeJsonTo()
    }

    fun createDevice(device: CreateDeviceEntity): DeviceItemDto {
        verifyCreateDeviceInput(device)
        return deviceDao.createDevice(device).getString(DEVICE_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun updateDevice(deviceId: Long, device: UpdateDeviceEntity): DeviceItemDto {
        verifyUpdateDeviceInput(device)
        return deviceDao.updateDevice(deviceId, device).getString(DEVICE_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun changeCategoryDevice(deviceId: Long, category: ChangeDeviceCategoryEntity): DeviceItemDto {
        return deviceDao.changeDeviceCategory(deviceId, category).getString(DEVICE_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deactivateDevice(deviceId: Long): DeviceItemDto {
        return deviceDao.deactivateDevice(deviceId).getString(DEVICE_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun activateDevice(deviceId: Long): DeviceItemDto {
        return deviceDao.activateDevice(deviceId).getString(DEVICE_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getRoomDevices(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, page: Int): DevicesDto {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return deviceDao.getRoomDevices(companyId, buildingId, roomId, elemsToSkip(page, DEVICES_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun getRoomDevice(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): DeviceQrCodeDto {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return deviceDao.getRoomDevice(companyId, buildingId, roomId, deviceId).deserializeJsonTo()
    }
}