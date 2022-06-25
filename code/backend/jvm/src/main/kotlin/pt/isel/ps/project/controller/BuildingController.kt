package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.building.*
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.BuildingResponses.BUILDING_MAX_PAGE_SIZE
import pt.isel.ps.project.responses.BuildingResponses.activateBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.changeBuildingManagerRepresentation
import pt.isel.ps.project.responses.BuildingResponses.createBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.deactivateBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.getBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.getBuildingsRepresentation
import pt.isel.ps.project.responses.BuildingResponses.updateBuildingRepresentation
import pt.isel.ps.project.service.BuildingService

@RestController
class BuildingController(private val service: BuildingService) {

    @GetMapping(Uris.Companies.Buildings.BASE_PATH)
    fun getBuildings(@PathVariable companyId: Long): QRreportJsonModel {
        val buildingsDto = service.getBuildings(companyId)
        return getBuildingsRepresentation(
            buildingsDto.buildings,
            companyId,
            CollectionModel(1, BUILDING_MAX_PAGE_SIZE, buildingsDto.buildingsCollectionSize),
            null
        )
    }

    @PostMapping(Uris.Companies.Buildings.BASE_PATH)
    fun createBuilding(
        @PathVariable companyId: Long,
        @RequestBody building: CreateBuildingEntity
    ): ResponseEntity<QRreportJsonModel> {
        return createBuildingRepresentation(companyId,service.createBuilding(companyId, building))
    }

    @GetMapping(Uris.Companies.Buildings.SPECIFIC_PATH)
    fun getBuilding(@PathVariable companyId: Long, @PathVariable buildingId: Long): ResponseEntity<QRreportJsonModel> {
        return getBuildingRepresentation(companyId, service.getBuilding(companyId, buildingId))
    }

    @PutMapping(Uris.Companies.Buildings.SPECIFIC_PATH)
    fun updateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody building: UpdateBuildingEntity
    ): ResponseEntity<QRreportJsonModel> {
        return updateBuildingRepresentation(companyId, service.updateBuilding(companyId, buildingId, building))
    }

    @DeleteMapping(Uris.Companies.Buildings.SPECIFIC_PATH)
    fun deactivateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long
    ): ResponseEntity<QRreportJsonModel> {
        return deactivateBuildingRepresentation(companyId, service.deactivateBuilding(companyId, buildingId))
    }

    @PutMapping(Uris.Companies.Buildings.ACTIVATE_PATH)
    fun activateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long
    ): ResponseEntity<QRreportJsonModel> {
        return activateBuildingRepresentation(companyId, service.activateBuilding(companyId, buildingId))
    }

    @PutMapping(Uris.Companies.Buildings.MANAGER_PATH)
    fun changeBuildingManager(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody manager: ChangeManagerEntity
    ): ResponseEntity<QRreportJsonModel> {
        return changeBuildingManagerRepresentation(
            companyId, service.changeBuildingManager(companyId, buildingId, manager)
        )
    }
}
