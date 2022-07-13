package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Device.activateDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.changeDeviceCategoryAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.createDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.deactivateDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.getDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.getDevicesAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.getRoomDeviceAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.getRoomDevicesAuthorization
import pt.isel.ps.project.auth.Authorizations.Device.updateDeviceAuthorization
import pt.isel.ps.project.model.Uris.Devices.ACTIVATE_PATH
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.DEVICES_PATH
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.SPECIFIC_DEVICE_PATH
import pt.isel.ps.project.model.Uris.Devices.BASE_PATH
import pt.isel.ps.project.model.Uris.Devices.CATEGORY_PATH
import pt.isel.ps.project.model.Uris.Devices.DEACTIVATE_PATH
import pt.isel.ps.project.model.Uris.Devices.SPECIFIC_PATH
import pt.isel.ps.project.model.device.*
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.room.RoomDeviceDto
import pt.isel.ps.project.responses.DeviceResponses.DEVICES_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.DeviceResponses.activateDeviceRepresentation
import pt.isel.ps.project.responses.DeviceResponses.changeDeviceCategoryRepresentation
import pt.isel.ps.project.responses.DeviceResponses.createDeviceRepresentation
import pt.isel.ps.project.responses.DeviceResponses.deactivateDeviceRepresentation
import pt.isel.ps.project.responses.DeviceResponses.getDeviceRepresentation
import pt.isel.ps.project.responses.DeviceResponses.getDevicesRepresentation
import pt.isel.ps.project.responses.DeviceResponses.getRoomDeviceRepresentation
import pt.isel.ps.project.responses.DeviceResponses.getRoomDevicesRepresentation
import pt.isel.ps.project.responses.DeviceResponses.updateDeviceRepresentation
import pt.isel.ps.project.service.DeviceService

@RestController
class DeviceController(val service: DeviceService) {

    @GetMapping(BASE_PATH)
    fun getDevices(@RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int, user: AuthPerson): QRreportJsonModel {
        getDevicesAuthorization(user)
        val devices = service.getDevices(page)
        return getDevicesRepresentation(
            user,
            devices.devices,
            CollectionModel(page, DEVICES_PAGE_MAX_SIZE, devices.devicesCollectionSize)
        )
    }

    @PostMapping(BASE_PATH)
    fun createDevice(@RequestBody device: CreateDeviceEntity, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        createDeviceAuthorization(user)
        return createDeviceRepresentation(service.createDevice(device))
    }

    @GetMapping(SPECIFIC_PATH)
    fun getDevice(@PathVariable deviceId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        getDeviceAuthorization(user)
        return getDeviceRepresentation(user, service.getDevice(deviceId))
    }

    @PutMapping(SPECIFIC_PATH)
    fun updateDevice(
        @PathVariable deviceId: Long,
        @RequestBody device: UpdateDeviceEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updateDeviceAuthorization(user)
        return updateDeviceRepresentation(service.updateDevice(deviceId, device))
    }

    @PostMapping(DEACTIVATE_PATH)
    fun deactivateDevice(@PathVariable deviceId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        deactivateDeviceAuthorization(user)
        return deactivateDeviceRepresentation(service.deactivateDevice(deviceId))
    }

    @PostMapping(ACTIVATE_PATH)
    fun activateDevice(@PathVariable deviceId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        activateDeviceAuthorization(user)
        return activateDeviceRepresentation(service.activateDevice(deviceId))
    }

    @PutMapping(CATEGORY_PATH)
    fun changeDeviceCategory(
        @PathVariable deviceId: Long,
        @RequestBody category: ChangeDeviceCategoryEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        changeDeviceCategoryAuthorization(user)
        return changeDeviceCategoryRepresentation(service.changeCategoryDevice(deviceId, category))
    }

    @GetMapping(DEVICES_PATH)
    fun getRoomDevices(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        getRoomDevicesAuthorization(user)
        val devices = service.getRoomDevices(user, companyId, buildingId, roomId, page)
        return getRoomDevicesRepresentation(
            user,
            companyId,
            buildingId,
            roomId,
            devices,
            CollectionModel(DEFAULT_PAGE, DEVICES_PAGE_MAX_SIZE, devices.devicesCollectionSize),
        )
    }

    @GetMapping(SPECIFIC_DEVICE_PATH)
    fun getRoomDevice(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        @PathVariable deviceId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        getRoomDeviceAuthorization(user)
        return getRoomDeviceRepresentation(
            user,
            companyId,
            buildingId,
            roomId,
            service.getRoomDevice(user, companyId, buildingId, roomId, deviceId)
        )
    }
}