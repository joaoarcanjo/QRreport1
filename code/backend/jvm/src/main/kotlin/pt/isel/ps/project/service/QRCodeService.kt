package pt.isel.ps.project.service

import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.QRCodeDao
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CHANGE_DENIED
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.model.Uris.REPORT_FORM_URL
import pt.isel.ps.project.model.qrcode.QRCODE_REP
import pt.isel.ps.project.model.qrcode.QRCodeDto
import pt.isel.ps.project.util.Hash.MD5.getHash
import pt.isel.ps.project.util.QRCode
import pt.isel.ps.project.util.Validator
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Person.belongsToCompany
import pt.isel.ps.project.util.Validator.Person.isBuildingManager
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class QRCodeService(val qrcodeDao: QRCodeDao) {

    fun getQRCode(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): ByteArrayResource {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        val hash = qrcodeDao.getQRHash(companyId, buildingId, roomId, deviceId)
        return QRCode.generate("$REPORT_FORM_URL$hash")
    }

    fun generateQRCode(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): ByteArrayResource {
        if (isManager(user) && !belongsToCompany(user, companyId) && !isBuildingManager(user, buildingId)) throw ForbiddenException(CHANGE_DENIED)
        val qrHash = getHash(roomId, deviceId)
        qrcodeDao.createQRHash(companyId, buildingId, roomId, deviceId, qrHash)
        return QRCode.generate("$REPORT_FORM_URL$qrHash")
    }

    fun getHashData(hash: String): QRCodeDto {
        return qrcodeDao.getHashData(hash).deserializeJsonTo()
    }
}