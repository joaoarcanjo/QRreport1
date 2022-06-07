package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.QRCode.BASE_PATH
import pt.isel.ps.project.model.qrcode.QRCodeDto
import pt.isel.ps.project.service.QRCodeService

@RestController
class QRCodeController(private val service: QRCodeService) {

    @GetMapping(BASE_PATH)
    fun getQRCode(@PathVariable roomId: Long, @PathVariable deviceId: Long): QRCodeDto {
        return service.getHash(roomId, deviceId)
    }

    @PostMapping(BASE_PATH)
    fun createQRCode(@PathVariable roomId: Long, @PathVariable deviceId: Long): QRCodeDto {
        return service.createHash(roomId, deviceId)
    }
}