package pt.isel.ps.project.model.company

import java.sql.Timestamp

data class CompanyItemDto(
    val id: Long,
    val name: String,
    val state: String,
    val timestamp: Timestamp,
    )

data class CompanyDto(
    val id: Long,
    val name: String,
    val state: String,
    val timestamp: Timestamp,
    val buildings: List<Any>?,
    val buildingsCollectionSize: Int,
)

data class CompaniesDto(
    val companies: List<CompanyItemDto>?,
    val companiesCollectionSize: Int,
)