package pt.isel.ps.project.controller

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.QRCode.BASE_PATH
import pt.isel.ps.project.service.QRCodeService

@RestController
class QRCodeController(private val service: QRCodeService) {

    private fun qrCodeResponse(byteArrayResource: ByteArrayResource) = ResponseEntity
        .ok()
        .header("Content-type", MediaType.IMAGE_PNG_VALUE)
        .body(byteArrayResource)

    @GetMapping(BASE_PATH)
    fun getQRCode(@PathVariable roomId: Long, @PathVariable deviceId: Long): ResponseEntity<ByteArrayResource> {
        return qrCodeResponse(service.getQRCode(roomId, deviceId))
    }

    @PostMapping(BASE_PATH)
    fun createQRCode(@PathVariable roomId: Long, @PathVariable deviceId: Long): ResponseEntity<ByteArrayResource> {
        return qrCodeResponse(service.generateQRCode(roomId, deviceId))
    }
}