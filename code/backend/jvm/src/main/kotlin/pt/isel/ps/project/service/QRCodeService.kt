package pt.isel.ps.project.service

import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.QRCodeDao
import pt.isel.ps.project.model.Uris.REPORT_FORM_URL
import pt.isel.ps.project.util.Hash.MD5.getHash
import pt.isel.ps.project.util.QRCode

@Service
class QRCodeService(val qrcodeDao: QRCodeDao) {

    fun getQRCode(roomId: Long, deviceId: Long): ByteArrayResource {
        val hash = qrcodeDao.getQRHash(roomId, deviceId)
        return QRCode.generate("$REPORT_FORM_URL$hash")
    }

    fun generateQRCode(roomId: Long, deviceId: Long): ByteArrayResource {
        val qrHash = getHash(roomId, deviceId)
        qrcodeDao.createQRHash(roomId, deviceId, qrHash)
        return QRCode.generate("$REPORT_FORM_URL$qrHash")
    }
}