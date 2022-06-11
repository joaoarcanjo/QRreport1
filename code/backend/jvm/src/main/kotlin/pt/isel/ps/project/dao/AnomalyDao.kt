package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.anomaly.ANOMALY_REP
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity

interface AnomalyDao {

    @SqlQuery("SELECT get_anomalies(:deviceId, null, null);") // :limit, :offset
    fun getAnomalies(deviceId: Long): String

    @SqlCall("CALL create_anomaly(:deviceId, :anomaly, :$ANOMALY_REP);")
    @OutParameter(name = ANOMALY_REP, sqlType = java.sql.Types.OTHER)
    fun createAnomaly(deviceId: Long, @BindBean anomaly: InputAnomalyEntity): OutParameters

    @SqlCall("CALL update_anomaly(:deviceId, :anomalyId, :anomaly, :$ANOMALY_REP);")
    @OutParameter(name = ANOMALY_REP, sqlType = java.sql.Types.OTHER)
    fun updateAnomaly(deviceId: Long, anomalyId: Long, @BindBean anomaly: InputAnomalyEntity): OutParameters

    @SqlCall("CALL delete_anomaly(:deviceId, :anomalyId, :$ANOMALY_REP);")
    @OutParameter(name = ANOMALY_REP, sqlType = java.sql.Types.OTHER)
    fun deleteAnomaly(deviceId: Long, anomalyId: Long): OutParameters
}