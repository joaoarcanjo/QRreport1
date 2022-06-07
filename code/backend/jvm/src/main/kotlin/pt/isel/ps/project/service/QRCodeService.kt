package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.QRCodeDao
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.qrcode.QRHASH_REP
import pt.isel.ps.project.model.qrcode.QRCodeDto
import pt.isel.ps.project.util.Hash.SHA256.getHashValue
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class QRCodeService(val qrHashDao: QRCodeDao) {

    fun getHash(roomId: Long, deviceId: Long): QRCodeDto {
        return qrHashDao.getQRHash(roomId, deviceId).deserializeJsonTo()
    }

    fun createHash(roomId: Long, deviceId: Long): QRCodeDto {
        return qrHashDao.createQRHash(roomId, deviceId, getHashValue(roomId, deviceId)).getString(QRHASH_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }
}