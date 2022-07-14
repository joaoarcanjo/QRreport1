package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.anomaly.ANOMALY_REP
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.responses.AnomalyResponses.ANOMALY_PAGE_MAX_SIZE

interface AnomalyDao {

    @SqlQuery("SELECT get_anomalies(:deviceId, $ANOMALY_PAGE_MAX_SIZE, :skip);")
    fun getAnomalies(deviceId: Long, skip: Int): String

    @SqlCall("CALL create_anomaly(:$ANOMALY_REP, :deviceId, :anomaly);")
    @OutParameter(name = ANOMALY_REP, sqlType = java.sql.Types.OTHER)
    fun createAnomaly(deviceId: Long, @BindBean anomaly: InputAnomalyEntity): OutParameters

    @SqlCall("CALL update_anomaly(:$ANOMALY_REP, :deviceId, :anomalyId, :anomaly);")
    @OutParameter(name = ANOMALY_REP, sqlType = java.sql.Types.OTHER)
    fun updateAnomaly(deviceId: Long, anomalyId: Long, @BindBean anomaly: InputAnomalyEntity): OutParameters

    @SqlCall("CALL delete_anomaly(:$ANOMALY_REP, :deviceId, :anomalyId);")
    @OutParameter(name = ANOMALY_REP, sqlType = java.sql.Types.OTHER)
    fun deleteAnomaly(deviceId: Long, anomalyId: Long): OutParameters
}