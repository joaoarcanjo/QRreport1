package pt.isel.ps.project.dao

import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transaction
import pt.isel.ps.project.model.qrcode.QRCODE_REP

interface QRCodeDao {
    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_room_device_hash(:companyId, :buildingId, :roomId, :deviceId)")
    fun getQRHash(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): String?

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = QRCODE_REP, sqlType = java.sql.Types.BOOLEAN)
    @SqlCall("CALL create_hash(:$QRCODE_REP, :companyId, :buildingId, :roomId, :deviceId, :hash);")
    fun createQRHash(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long, hash: String)

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_hash_data(:hash)")
    fun getHashData(hash: String): String
}