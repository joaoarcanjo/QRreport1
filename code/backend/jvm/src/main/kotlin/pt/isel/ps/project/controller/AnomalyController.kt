package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Devices.Anomalies.BASE_PATH
import pt.isel.ps.project.model.Uris.Devices.Anomalies.SPECIFIC_PATH
import pt.isel.ps.project.model.anomaly.AnomaliesDto
import pt.isel.ps.project.model.anomaly.AnomalyItemDto
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.service.AnomalyService

@RestController
class AnomalyController(private val service: AnomalyService) {

    @GetMapping(BASE_PATH)
    fun getAnomalies(@PathVariable deviceId: Long): AnomaliesDto {
        return service.getAnomalies(deviceId)
    }

    @PostMapping(BASE_PATH)
    fun createAnomaly(@PathVariable deviceId: Long, @RequestBody anomaly: InputAnomalyEntity): AnomalyItemDto {
        return service.createAnomaly(deviceId, anomaly)
    }

    @PutMapping(SPECIFIC_PATH)
    fun updateAnomaly(
        @PathVariable deviceId: Long,
        @PathVariable anomalyId: Long,
        @RequestBody anomaly: InputAnomalyEntity
    ): AnomalyItemDto {
        return service.updateAnomaly(deviceId, anomalyId, anomaly)
    }

    @DeleteMapping(SPECIFIC_PATH)
    fun deleteAnomaly(@PathVariable deviceId: Long, @PathVariable anomalyId: Long): AnomalyItemDto {
        return service.deleteAnomaly(deviceId, anomalyId)
    }
}