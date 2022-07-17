package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.building.BUILDING_REP
import pt.isel.ps.project.model.building.ChangeManagerEntity
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.company.COMPANY_REP
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.responses.BuildingResponses.BUILDING_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.RoomResponses
import java.util.*

interface BuildingDao {

    @SqlQuery("SELECT get_buildings(:companyId, $BUILDING_PAGE_MAX_SIZE, :skip);")
    fun getBuildings(companyId: Long, skip: Int): String

    @SqlCall("CALL create_building(:$BUILDING_REP, :companyId, :name, :floors, :manager);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun createBuilding(companyId: Long, @BindBean building: CreateBuildingEntity): OutParameters

    @SqlQuery("SELECT get_building(:companyId, :buildingId, ${BUILDING_PAGE_MAX_SIZE}, :skip);")
    fun getBuilding(companyId: Long, buildingId: Long, skip: Int): String

    @SqlCall("CALL update_building(:$BUILDING_REP, :companyId, :buildingId, :name, :floors);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun updateBuilding(companyId: Long, buildingId: Long, @BindBean building: UpdateBuildingEntity): OutParameters

    @SqlCall("CALL deactivate_building(:$BUILDING_REP, :companyId, :buildingId);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateBuilding(companyId: Long, buildingId: Long): OutParameters

    @SqlCall("CALL activate_building(:$BUILDING_REP, :companyId, :buildingId);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun activateBuilding(companyId: Long, buildingId: Long): OutParameters

    @SqlCall("CALL change_building_manager(:$BUILDING_REP, :companyId, :buildingId, :manager);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun changeBuildingManager(companyId: Long, buildingId: Long, @BindBean manager: ChangeManagerEntity): OutParameters
}