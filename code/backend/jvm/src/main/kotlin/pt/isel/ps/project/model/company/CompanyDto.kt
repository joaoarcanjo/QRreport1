package pt.isel.ps.project.model.company

import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.building.BuildingsDto
import java.sql.Timestamp

data class CompanyItemDto(
    val id: Long,
    val name: String,
    val state: String,
    val timestamp: Timestamp?,
)

data class CompanyDto(
    val id: Long,
    val name: String,
    val state: String,
    val timestamp: Timestamp?,
    val buildings: BuildingsDto?,
    /*val buildingsCollectionSize: Int?,
    val companyState: String?,*/
)

data class CompaniesDto(
    val companies: List<CompanyItemDto>?,
    val companiesCollectionSize: Int,
)

fun CompanyDto.removeBuildings() = CompanyDto(id, name, state, timestamp, null)
