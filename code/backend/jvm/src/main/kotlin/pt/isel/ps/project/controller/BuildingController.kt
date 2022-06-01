package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.building.*
import pt.isel.ps.project.service.BuildingService

@RestController
class BuildingController(private val service: BuildingService) {

    @GetMapping(Uris.Companies.Buildings.BASE_PATH)
    fun getBuildings(@PathVariable companyId: Long): BuildingsDto {
        return service.getBuildings(companyId)
    }

    @PostMapping(Uris.Companies.Buildings.BASE_PATH)
    fun createBuilding(@PathVariable companyId: Long, @RequestBody building: CreateBuildingEntity): BuildingItemDto {
        return service.createBuilding(companyId, building)
    }

    @GetMapping(Uris.Companies.Buildings.SPECIFIC_PATH)
    fun getBuilding(@PathVariable companyId: Long, @PathVariable buildingId: Long): BuildingDto {
        return service.getBuilding(companyId, buildingId)
    }

    @PutMapping(Uris.Companies.Buildings.SPECIFIC_PATH)
    fun updateBuilding(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody building: UpdateBuildingEntity
    ): BuildingItemDto {
        return service.updateBuilding(companyId, buildingId, building)
    }

    @DeleteMapping(Uris.Companies.Buildings.SPECIFIC_PATH)
    fun deactivateBuilding(@PathVariable companyId: Long, @PathVariable buildingId: Long): BuildingItemDto {
        return service.deactivateBuilding(companyId, buildingId)
    }

    @PutMapping(Uris.Companies.Buildings.ACTIVATE_PATH)
    fun activateBuilding(@PathVariable companyId: Long, @PathVariable buildingId: Long): BuildingItemDto {
        return service.activateBuilding(companyId, buildingId)
    }

    @PutMapping(Uris.Companies.Buildings.MANAGER_PATH)
    fun changeBuildingManager(
        @PathVariable companyId: Long,
        @PathVariable buildingId: Long,
        @RequestBody manager: ChangeManagerEntity
    ): BuildingManagerDto {
        return service.changeBuildingManager(companyId, buildingId, manager)
    }
}
