package pt.isel.ps.project.dao

import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.qrcode.QRCodeDto

interface QRCodeDao {

    @SqlQuery("SELECT qr_hash FROM HASH WHERE room = :roomId AND device = :deviceId;")
    fun getQRHash(roomId: Long, deviceId: Long): String

    @SqlCall("CALL create_hash(:roomId, :deviceId, :hash, null);")
    fun createQRHash(roomId: Long, deviceId: Long, hash: String)
}