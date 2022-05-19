package pt.isel.ps.project.model.company

/*
 * Name of the company representation output parameter
 */
const val COMPANY_REP = "companyRep"

object CompanyEntity {
    const val COMPANY_NAME = "name"
    const val COMPANY_NAME_MAX_CHARS = 50
}

data class CreateCompanyEntity(
    val name: String,
)

data class UpdateCompanyEntity(
    var id: Long?,
    val name: String?,
)
