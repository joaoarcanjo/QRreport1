package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Devices.Anomalies
import pt.isel.ps.project.model.Uris.Devices.Anomalies.ANOMALIES_PAGINATION
import pt.isel.ps.project.model.anomaly.AnomaliesDto
import pt.isel.ps.project.model.anomaly.AnomalyItemDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin

object AnomalyResponses {
    const val ANOMALY_PAGE_MAX_SIZE = 10

    object Actions {
        fun createAnomaly(deviceId: Long) = QRreportJsonModel.Action(
            name = "create-anomaly",
            title = "Create anomaly",
            method = HttpMethod.POST,
            href = Anomalies.makeBase(deviceId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("anomaly", "string"),
            )
        )

        fun updateAnomaly(deviceId: Long, anomalyId: Long) = QRreportJsonModel.Action(
            name = "update-anomaly",
            title = "Update anomaly",
            method = HttpMethod.PUT,
            href = Anomalies.makeSpecific(deviceId, anomalyId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("anomaly", "string")
            )
        )

        fun deleteAnomaly(deviceId: Long, anomalyId: Long) = QRreportJsonModel.Action(
            name = "delete-anomaly",
            title = "Delete anomaly",
            method = HttpMethod.DELETE,
            href = Anomalies.makeSpecific(deviceId, anomalyId),
        )
    }

    private fun getAnomalyItem(user: AuthPerson?, anomaly: AnomalyItemDto, deviceId: Long, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.ANOMALY),
        rel = rel,
        properties = anomaly,
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (user == null || !isAdmin(user)) return@apply
            add(Actions.updateAnomaly(deviceId, anomaly.id))
            add(Actions.deleteAnomaly(deviceId, anomaly.id))
        },
        links = listOf(Links.self(Anomalies.makeSpecific(deviceId, anomaly.id)))
    )

    fun getAnomaliesRepresentation(
        user: AuthPerson?,
        anomaliesDto: AnomaliesDto,
        deviceId: Long,
        collection: CollectionModel,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.ANOMALY, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (anomaliesDto.anomalies != null) addAll(anomaliesDto.anomalies.map {
                getAnomalyItem(user, it, deviceId, listOf(Relations.ITEM))
            })
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (user != null && isAdmin(user)) add(Actions.createAnomaly(deviceId))
        },
        links = listOf(
            Links.self(Uris.makePagination(collection.pageIndex, Anomalies.makeBase(deviceId))),
            Links.pagination(ANOMALIES_PAGINATION),
        )
    )

    fun createAnomalyRepresentation(deviceId: Long, anomaly: AnomalyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ANOMALY),
            properties = anomaly,
            links = listOf(Links.anomalies(deviceId))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Anomalies.makeBase(deviceId))
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