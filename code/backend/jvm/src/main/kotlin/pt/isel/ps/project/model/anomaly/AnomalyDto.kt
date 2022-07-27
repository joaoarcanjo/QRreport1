package pt.isel.ps.project.model.anomaly

data class AnomalyItemDto(
    val id: Long,
    val anomaly: String
)

data class AnomaliesDto(
    val anomalies: List<AnomalyItemDto>?,
    val anomaliesCollectionSize: Int,
    val deviceState: String,
)