package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Devices.ACTIVATE_PATH
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.DEVICES_PATH
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.SPECIFIC_DEVICE_PATH
import pt.isel.ps.project.model.Uris.Devices.BASE_PATH
import pt.isel.ps.project.model.Uris.Devices.CATEGORY_PATH
import pt.isel.ps.project.model.Uris.Devices.SPECIFIC_PATH
import pt.isel.ps.project.model.device.*
import pt.isel.ps.project.service.DeviceService

@RestController
class DeviceController(val service: DeviceService) {

    @GetMapping(BASE_PATH)
    fun getDevices(): DevicesDto {
        return service.getDevices()
    }

    @GetMapping(SPECIFIC_PATH)
    fun getDevices(@PathVariable deviceId: Int): DeviceDto {
        return service.getDevice(deviceId)
    }

    @PostMapping(BASE_PATH)
    fun createDevice(@RequestBody device: CreateDeviceEntity): DeviceItemDto {

        return service.createDevice(device)
    }

    @PutMapping(SPECIFIC_PATH)
    fun updateDevice(@PathVariable deviceId: Int, @RequestBody device: UpdateDeviceEntity): DeviceItemDto {
        return service.updateDevice(deviceId, device)
    }

    @PutMapping(CATEGORY_PATH)
    fun changeDeviceCategory(@PathVariable deviceId: Int, @RequestBody category: ChangeDeviceCategoryEntity): DeviceItemDto {
        return service.changeCategoryDevice(deviceId, category)
    }

    @DeleteMapping(SPECIFIC_PATH)
    fun deactivateDevice(@PathVariable deviceId: Int): DeviceItemDto {
        return service.deactivateDevice(deviceId)
    }

    @PutMapping(ACTIVATE_PATH)
    fun activateDevice(@PathVariable deviceId: Int): DeviceItemDto {
        return service.activateDevice(deviceId)
    }

    @GetMapping(DEVICES_PATH)
    fun getRoomDevices(@PathVariable roomId: Int): DevicesDto {
        return service.getRoomDevices(roomId)
    }

    @GetMapping(SPECIFIC_DEVICE_PATH)
    fun getRoomDevice(@PathVariable roomId: Int, @PathVariable deviceId: Int): RoomDeviceDto {
        return service.getRoomDevice(roomId, deviceId)
    }
}