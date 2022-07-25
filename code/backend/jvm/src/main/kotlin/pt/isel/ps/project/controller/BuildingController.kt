package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Building.activateBuildingAuthorization
import pt.isel.ps.project.auth.Authorizations.Building.changeBuildingManagerAuthorization
import pt.isel.ps.project.auth.Authorizations.Building.createBuildingAuthorization
import pt.isel.ps.project.auth.Authorizations.Building.deactivateBuildingAuthorization
import pt.isel.ps.project.auth.Authorizations.Building.getBuildingAuthorization
import pt.isel.ps.project.auth.Authorizations.Building.getBuildingsAuthorization
import pt.isel.ps.project.auth.Authorizations.Building.updateBuildingAuthorization
import pt.isel.ps.project.model.Uris.Companies.Buildings
import pt.isel.ps.project.model.building.*
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.BuildingResponses.BUILDING_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.BuildingResponses.changeBuildingManagerRepresentation
import pt.isel.ps.project.responses.BuildingResponses.createBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.deactivateActivateBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.getBuildingRepresentation
import pt.isel.ps.project.responses.BuildingResponses.getBuildingsRepresentation
import pt.isel.ps.project.responses.BuildingResponses.updateBuildingRepresentation
import pt.isel.ps.project.service.BuildingService

@RestController
class BuildingController(private val service: BuildingService) {

    @GetMapping(Buildings.BASE_PATH)
    fun getBuildings(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        @PathVariable companyId: Long,
        user: AuthPerson,
    ): QRreportJsonModel {
        getBuildingsAuthorization(user)
        val buildingsDto = service.getBuildings(user, companyId, page)
        return getBuildingsRepresentation(user, buildingsDto, companyId, page, null)
    }

    @PostMapping(Buildings.BASE_PATH)
    fun createBuilding(
        @PathVariable companyId: Long,
        @RequestBody building: CreateBuildingEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        createBuildingAuthorization(user)
        return createBuildingRepresentation(companyId, service.createBuilding(user, companyId, building))
    }

    @GetMapping(Buildings.SPECIFIC_PATH)
    fun getBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        getBuildingAuthorization(user)
        return getBuildingRepresentation(user, companyId, service.getBuilding(user, companyId, buildingId))
    }

    @PutMapping(Buildings.SPECIFIC_PATH)
    fun updateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody building: UpdateBuildingEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updateBuildingAuthorization(user)
        return updateBuildingRepresentation(companyId, service.updateBuilding(user, companyId, buildingId, building))
    }

    @PostMapping(Buildings.DEACTIVATE_PATH)
    fun deactivateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        deactivateBuildingAuthorization(user)
        return deactivateActivateBuildingRepresentation(companyId, service.deactivateBuilding(user, companyId, buildingId))
    }

    @PostMapping(Buildings.ACTIVATE_PATH)
    fun activateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        activateBuildingAuthorization(user)
        return deactivateActivateBuildingRepresentation(companyId, service.activateBuilding(user, companyId, buildingId))
    }

    @PutMapping(Buildings.MANAGER_PATH)
    fun changeBuildingManager(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody manager: ChangeManagerEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        changeBuildingManagerAuthorization(user)
        return changeBuildingManagerRepresentation(
            companyId,
            service.changeBuildingManager(user, companyId, buildingId, manager)
        )
    }
}
