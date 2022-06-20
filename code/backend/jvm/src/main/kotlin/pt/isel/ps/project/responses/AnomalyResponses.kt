package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.anomaly.AnomaliesDto
import pt.isel.ps.project.model.anomaly.AnomalyItemDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader

object AnomalyResponses {
    const val ANOMALY_PAGE_MAX_SIZE = 10

    object Actions {
        fun createAnomaly(deviceId: Long) = QRreportJsonModel.Action(
            name = "create-anomaly",
            title = "Create new anomaly",
            method = HttpMethod.POST,
            href = Uris.Devices.Anomalies.makeBase(deviceId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("anomaly", "string", required = true)
            )
        )

        fun updateAnomaly(projectId: Long, anomalyId: Long) = QRreportJsonModel.Action(
            name = "update-anomaly",
            title = "Update anomaly",
            method = HttpMethod.PUT,
            href = Uris.Devices.Anomalies.makeSpecific(projectId, anomalyId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("anomaly", "string", required = true)
            )
        )

        fun deleteAnomaly(projectId: Long, anomalyId: Long) = QRreportJsonModel.Action(
            name = "delete-anomaly",
            title = "Delete anomaly",
            method = HttpMethod.DELETE,
            href = Uris.Devices.Anomalies.makeSpecific(projectId, anomalyId),
        )
    }

    private fun getAnomalyItem(anomaly: AnomalyItemDto, deviceId: Long, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.ANOMALY),
        rel = rel,
        properties = anomaly,
        actions = listOf(Actions.updateAnomaly(deviceId, anomaly.id), Actions.deleteAnomaly(deviceId, anomaly.id)),
        links = listOf(Links.self(Uris.Devices.Anomalies.makeSpecific(deviceId, anomaly.id)))
    )

    fun getAnomaliesRepresentation(
        anomaliesDto: AnomaliesDto,
        deviceId: Long,
        collection: CollectionModel,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.ANOMALY, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (anomaliesDto.anomalies != null) addAll(anomaliesDto.anomalies.map { getAnomalyItem(it, deviceId, listOf(Relations.ITEM)) })
        },
        actions = listOf(Actions.createAnomaly(deviceId)),
        links = listOf(Links.self(Uris.Devices.Anomalies.makeBase(deviceId)))
    )

    fun createAnomalyRepresentation(deviceId: Long, anomaly: AnomalyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ANOMALY),
            properties = anomaly,
            links = listOf(Links.anomalies(deviceId))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Devices.Anomalies.makeSpecific(deviceId, anomaly.id))
    )

    fun updateAnomalyRepresentation(deviceId: Long, anomaly: AnomalyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ANOMALY),
            properties = anomaly,
            links = listOf(Links.anomalies(deviceId))
        )
    )

    fun deleteAnomalyRepresentation(deviceId: Long, anomaly: AnomalyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ANOMALY),
            properties = anomaly,
            links = listOf(Links.anomalies(deviceId))
        )
    )
}