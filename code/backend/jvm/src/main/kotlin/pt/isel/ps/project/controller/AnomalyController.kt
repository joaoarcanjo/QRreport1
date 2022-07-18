package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Anomaly.createAnomalyAuthorization
import pt.isel.ps.project.auth.Authorizations.Anomaly.deleteAnomalyAuthorization
import pt.isel.ps.project.auth.Authorizations.Anomaly.updateAnomalyAuthorization
import pt.isel.ps.project.model.Uris.Devices.Anomalies
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.AnomalyResponses.ANOMALY_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.AnomalyResponses.createAnomalyRepresentation
import pt.isel.ps.project.responses.AnomalyResponses.deleteAnomalyRepresentation
import pt.isel.ps.project.service.AnomalyService
import pt.isel.ps.project.responses.AnomalyResponses.getAnomaliesRepresentation
import pt.isel.ps.project.responses.AnomalyResponses.updateAnomalyRepresentation

@RestController
class AnomalyController(private val service: AnomalyService) {

    @GetMapping(Anomalies.BASE_PATH)
    fun getAnomalies(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        @PathVariable deviceId: Long,
        user: AuthPerson?,
    ): QRreportJsonModel {
        val anomalies = service.getAnomalies(deviceId, page)
        return getAnomaliesRepresentation(
            user,
            anomalies,
            deviceId,
            CollectionModel(page, ANOMALY_PAGE_MAX_SIZE, anomalies.anomaliesCollectionSize),
            null
        )
    }

    @PostMapping(Anomalies.BASE_PATH)
    fun createAnomaly(
        @PathVariable deviceId: Long,
        @RequestBody anomaly: InputAnomalyEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        createAnomalyAuthorization(user)
        return createAnomalyRepresentation(deviceId, service.createAnomaly(deviceId, anomaly))
    }

    @PutMapping(Anomalies.SPECIFIC_PATH)
    fun updateAnomaly(
        @PathVariable deviceId: Long,
        @PathVariable anomalyId: Long,
        @RequestBody anomaly: InputAnomalyEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updateAnomalyAuthorization(user)
        return updateAnomalyRepresentation(deviceId, service.updateAnomaly(deviceId, anomalyId, anomaly))
    }

    @DeleteMapping(Anomalies.SPECIFIC_PATH)
    fun deleteAnomaly(@PathVariable deviceId: Long, @PathVariable anomalyId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        deleteAnomalyAuthorization(user)
        return deleteAnomalyRepresentation(deviceId, service.deleteAnomaly(deviceId, anomalyId))
    }
}