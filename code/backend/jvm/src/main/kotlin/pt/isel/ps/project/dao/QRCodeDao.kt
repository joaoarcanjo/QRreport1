package pt.isel.ps.project.dao

import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.qrcode.QRCODE_REP

interface QRCodeDao {

    @SqlQuery("SELECT get_room_device_hash(:companyId, :buildingId, :roomId, :deviceId)")
    fun getQRHash(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): String

    @OutParameter(name = QRCODE_REP, sqlType = java.sql.Types.BOOLEAN)
    @SqlCall("CALL create_hash(:$QRCODE_REP, :companyId, :buildingId, :roomId, :deviceId, :hash);")
    fun createQRHash(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long, hash: String)

    @SqlQuery("SELECT get_hash_data(:hash)")
    fun getHashData(hash: String): String
}