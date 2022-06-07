package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.qrcode.QRHASH_REP

interface QRCodeDao {

    @SqlQuery("SELECT get_hash(:roomId, :deviceId);")
    fun getQRHash(roomId: Long, deviceId: Long): String

    @SqlCall("CALL create_hash(:roomId, :deviceId, :hash, :$QRHASH_REP);")
    @OutParameter(name = QRHASH_REP, sqlType = java.sql.Types.OTHER)
    fun createQRHash(roomId: Long, deviceId: Long, hash: String): OutParameters
}