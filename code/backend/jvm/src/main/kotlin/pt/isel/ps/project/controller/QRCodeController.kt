package pt.isel.ps.project.controller

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.QRCode.createQRCodeAuthorization
import pt.isel.ps.project.auth.Authorizations.QRCode.getQRCodeAuthorization
import pt.isel.ps.project.model.Uris.QRCode
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.QRCodeResponses.getReportFormResponse
import pt.isel.ps.project.service.QRCodeService

@RestController
class QRCodeController(private val service: QRCodeService) {

    private fun qrCodeResponse(byteArrayResource: ByteArrayResource) = ResponseEntity
        .ok()
        .contentType(MediaType.IMAGE_PNG)
        .body(byteArrayResource)

    @GetMapping(QRCode.BASE_PATH)
    fun getQRCode(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        @PathVariable deviceId: Long,
        user: AuthPerson,
    ): ResponseEntity<ByteArrayResource> {
        getQRCodeAuthorization(user)
        return qrCodeResponse(service.getQRCode(user, companyId, buildingId, roomId, deviceId))
    }

    @PostMapping(QRCode.BASE_PATH)
    fun createQRCode(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @PathVariable roomId: Long,
        @PathVariable deviceId: Long,
        user: AuthPerson,
    ): ResponseEntity<ByteArrayResource> {
        createQRCodeAuthorization(user)
        return qrCodeResponse(service.generateQRCode(user, companyId, buildingId, roomId, deviceId))
    }

    @GetMapping(QRCode.REPORT_PATH)
    fun getReportForm(@PathVariable hash: String, user: AuthPerson?): ResponseEntity<QRreportJsonModel> {
        return getReportFormResponse(user, hash, service.getHashData(hash))
    }
}