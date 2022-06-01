package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.building.BUILDING_REP
import pt.isel.ps.project.model.building.ChangeManagerEntity
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.company.COMPANY_REP
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity

interface BuildingDao {

    @SqlQuery("SELECT get_buildings(:companyId, null, null);") // :limit, :offset
    fun getBuildings(companyId: Long): String

    @SqlCall("CALL create_building(:companyId, :name, :floors, :manager, :$BUILDING_REP);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun createBuilding(companyId: Long, @BindBean building: CreateBuildingEntity): OutParameters

    @SqlQuery("SELECT get_building(:companyId, :buildingId);")
    fun getBuilding(companyId: Long, buildingId: Long): String

    @SqlCall("CALL update_building(:companyId, :buildingId, :$BUILDING_REP, :name, :floors);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun updateBuilding(companyId: Long, buildingId: Long, @BindBean building: UpdateBuildingEntity): OutParameters

    @SqlCall("CALL deactivate_building(:companyId, :buildingId, :$BUILDING_REP);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateBuilding(companyId: Long, buildingId: Long): OutParameters

    @SqlCall("CALL activate_building(:companyId, :buildingId, :$BUILDING_REP);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun activateBuilding(companyId: Long, buildingId: Long): OutParameters

    @SqlCall("CALL change_building_manager(:companyId, :buildingId, :manager, :$BUILDING_REP);")
    @OutParameter(name = BUILDING_REP, sqlType = java.sql.Types.OTHER)
    fun changeBuildingManager(companyId: Long, buildingId: Long, @BindBean manager: ChangeManagerEntity): OutParameters
}