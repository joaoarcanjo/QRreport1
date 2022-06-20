package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Devices.Anomalies.BASE_PATH
import pt.isel.ps.project.model.Uris.Devices.Anomalies.SPECIFIC_PATH
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.AnomalyResponses.ANOMALY_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.AnomalyResponses.createAnomalyRepresentation
import pt.isel.ps.project.responses.AnomalyResponses.deleteAnomalyRepresentation
import pt.isel.ps.project.service.AnomalyService
import pt.isel.ps.project.responses.AnomalyResponses.getAnomaliesRepresentation
import pt.isel.ps.project.responses.AnomalyResponses.updateAnomalyRepresentation

@RestController
class AnomalyController(private val service: AnomalyService) {

    @GetMapping(BASE_PATH)
    fun getAnomalies(@PathVariable deviceId: Long): QRreportJsonModel {
        val anomalies = service.getAnomalies(deviceId)
        return getAnomaliesRepresentation(
            anomalies,
            deviceId,
            CollectionModel(10, ANOMALY_PAGE_MAX_SIZE, anomalies.anomaliesCollectionSize),
            null
        )
    }

    @PostMapping(BASE_PATH)
    fun createAnomaly(
        @PathVariable deviceId: Long,
        @RequestBody anomaly: InputAnomalyEntity
    ): ResponseEntity<QRreportJsonModel> {
        return createAnomalyRepresentation(deviceId, service.createAnomaly(deviceId, anomaly))
    }

    @PutMapping(SPECIFIC_PATH)
    fun updateAnomaly(
        @PathVariable deviceId: Long,
        @PathVariable anomalyId: Long,
        @RequestBody anomaly: InputAnomalyEntity
    ): ResponseEntity<QRreportJsonModel> {
        return updateAnomalyRepresentation(deviceId, service.updateAnomaly(deviceId, anomalyId, anomaly))
    }

    @DeleteMapping(SPECIFIC_PATH)
    fun deleteAnomaly(@PathVariable deviceId: Long, @PathVariable anomalyId: Long): ResponseEntity<QRreportJsonModel> {
        return deleteAnomalyRepresentation(deviceId, service.deleteAnomaly(deviceId, anomalyId))
    }
}