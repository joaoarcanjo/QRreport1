package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.BuildingDao
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CHANGE_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CREATION_DENIED
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.building.BuildingsDto
import pt.isel.ps.project.model.building.BuildingDto
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.building.BUILDING_REP
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.building.ChangeManagerEntity
import pt.isel.ps.project.model.building.BuildingManagerDto
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.responses.BuildingResponses.BUILDING_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.RoomResponses
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Company.Building.verifyCreateBuildingInput
import pt.isel.ps.project.util.Validator.Company.Building.verifyUpdateBuildingInput
import pt.isel.ps.project.util.Validator.Person.belongsToCompany
import pt.isel.ps.project.util.Validator.Person.isBuildingManager
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class BuildingService(val buildingDao: BuildingDao) {

    fun getBuildings(user: AuthPerson, companyId: Long, page: Int): BuildingsDto {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return buildingDao.getBuildings(companyId, elemsToSkip(page, BUILDING_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun createBuilding(user: AuthPerson, companyId: Long, building: CreateBuildingEntity): BuildingItemDto {
        verifyCreateBuildingInput(building)
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(CREATION_DENIED)
        return buildingDao.createBuilding(companyId, building).getString(BUILDING_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getBuilding(user: AuthPerson, companyId: Long, buildingId: Long): BuildingDto {
        if (isManager(user) && !belongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return buildingDao.getBuilding(companyId, buildingId).deserializeJsonTo()
    }

    fun updateBuilding(user: AuthPerson, companyId: Long, buildingId: Long, building: UpdateBuildingEntity): BuildingItemDto {
        verifyUpdateBuildingInput(building)
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId)) throw ForbiddenException(CHANGE_DENIED)
        return buildingDao.updateBuilding(companyId, buildingId, building).getString(BUILDING_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deactivateBuilding(user: AuthPerson, companyId: Long, buildingId: Long): BuildingItemDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId)) throw ForbiddenException(CHANGE_DENIED)
        return buildingDao.deactivateBuilding(companyId, buildingId).getString(BUILDING_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun activateBuilding(user: AuthPerson, companyId: Long, buildingId: Long): BuildingItemDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId)) throw ForbiddenException(CHANGE_DENIED)
        return buildingDao.activateBuilding(companyId, buildingId).getString(BUILDING_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun changeBuildingManager(user: AuthPerson, companyId: Long, buildingId: Long, manager: ChangeManagerEntity): BuildingManagerDto {
        if (isManager(user) && !isBuildingManager(user, companyId, buildingId)) throw ForbiddenException(CHANGE_DENIED)
        return buildingDao.changeBuildingManager(companyId, buildingId, manager).getString(BUILDING_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}