package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Devices.ACTIVATE_PATH
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.DEVICES_PATH
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.SPECIFIC_DEVICE_PATH
import pt.isel.ps.project.model.Uris.Devices.BASE_PATH
import pt.isel.ps.project.model.Uris.Devices.CATEGORY_PATH
import pt.isel.ps.project.model.Uris.Devices.SPECIFIC_PATH
import pt.isel.ps.project.model.device.*
import pt.isel.ps.project.model.representations.CollectionModel
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
    fun getDevices(): QRreportJsonModel {
        val devices = service.getDevices()
        return getDevicesRepresentation(
            devices.devices,
            CollectionModel(1, DEVICES_PAGE_MAX_SIZE, devices.devicesCollectionSize)
        )
    }

    @GetMapping(SPECIFIC_PATH)
    fun getDevices(@PathVariable deviceId: Long): ResponseEntity<QRreportJsonModel> {
        return getDeviceRepresentation(service.getDevice(deviceId))
    }

    @PostMapping(BASE_PATH)
    fun createDevice(@RequestBody device: CreateDeviceEntity): ResponseEntity<QRreportJsonModel> {
        return createDeviceRepresentation(service.createDevice(device))
    }

    @PutMapping(SPECIFIC_PATH)
    fun updateDevice(@PathVariable deviceId: Long, @RequestBody device: UpdateDeviceEntity): ResponseEntity<QRreportJsonModel> {
        return updateDeviceRepresentation(service.updateDevice(deviceId, device))
    }

    @PutMapping(CATEGORY_PATH)
    fun changeDeviceCategory(@PathVariable deviceId: Long, @RequestBody category: ChangeDeviceCategoryEntity): ResponseEntity<QRreportJsonModel> {
        return changeDeviceCategoryRepresentation(service.changeCategoryDevice(deviceId, category))
    }

    @DeleteMapping(SPECIFIC_PATH)
    fun deactivateDevice(@PathVariable deviceId: Long): ResponseEntity<QRreportJsonModel> {
        return deactivateDeviceRepresentation(service.deactivateDevice(deviceId))
    }

    @PutMapping(ACTIVATE_PATH)
    fun activateDevice(@PathVariable deviceId: Long): ResponseEntity<QRreportJsonModel> {
        return activateDeviceRepresentation(service.activateDevice(deviceId))
    }

    @GetMapping(DEVICES_PATH)
    fun getRoomDevices(@PathVariable roomId: Long): ResponseEntity<QRreportJsonModel> {
        val devices = service.getRoomDevices(roomId)
        return getRoomDevicesRepresentation(
            devices,
            CollectionModel(1, DEVICES_PAGE_MAX_SIZE, devices.devicesCollectionSize),
            roomId
        )
    }

    //Todo, não funciona,
    @GetMapping(SPECIFIC_DEVICE_PATH)
    fun getRoomDevice(@PathVariable roomId: Long, @PathVariable deviceId: Long): QRreportJsonModel {
        return getRoomDeviceRepresentation(roomId, service.getRoomDevice(roomId, deviceId))
    }
}