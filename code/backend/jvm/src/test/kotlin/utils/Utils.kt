package utils

import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.company.CompaniesDto
import pt.isel.ps.project.model.company.CompanyDto
import pt.isel.ps.project.model.company.CompanyItemDto
import java.io.File

object Utils {
    object LoadScript {
        private val classLoader = javaClass.classLoader
        fun getResourceFile(resourceName: String) =
            File(classLoader.getResource(resourceName)!!.toURI()).readText()
    }
}

fun AuthPerson.ignoreTimestamp() = AuthPerson(id, name, phone, email, activeRole, skills, companies, null, state, reason)
fun BuildingItemDto.ignoreTimestamp() = BuildingItemDto(id, name, floors, state, null)
fun CompanyDto.ignoreTimestamp(): CompanyDto {
    val buildings = buildings?.map { it.ignoreTimestamp() }
    return CompanyDto(id, name, state, null, buildings, buildingsCollectionSize)
}
fun CompanyItemDto.ignoreTimestamp() = CompanyItemDto(id, name, state, null)
fun CompaniesDto.ignoreTimestamps() = CompaniesDto(
    companies?.map { it.ignoreTimestamp() },
    companiesCollectionSize
)
