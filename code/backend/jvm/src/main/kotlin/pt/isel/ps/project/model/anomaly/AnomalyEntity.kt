package pt.isel.ps.project.model.anomaly

import java.util.*

/*
 * Name of the anomaly representation output parameter
 */
const val ANOMALY_REP = "anomalyRep"

object AnomalyEntity {
    const val ANOMALY_ANOMALY = "anomaly"
    const val ANOMALY_ANOMALY_MAX_CHARS = 150
}

data class InputAnomalyEntity(
    val anomaly: String
)