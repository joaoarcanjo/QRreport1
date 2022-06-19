package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.company.CompaniesDto
import pt.isel.ps.project.model.company.CompanyDto
import pt.isel.ps.project.model.company.CompanyItemDto
import pt.isel.ps.project.model.company.removeBuildings
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildCollectionLinks
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader

object CompanyResponses {
    const val COMPANY_PAGE_MAX_SIZE = 10

    object Actions {
        fun createCompany() = QRreportJsonModel.Action(
            name = "create-company",
            title = "Create a company",
            method = HttpMethod.POST,
            href = Uris.Companies.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "name", type = "string"),
            )
        )

        fun updateCompany(companyId: Long) = QRreportJsonModel.Action(
            name = "update-company",
            title = "Update company",
            method = HttpMethod.PUT,
            href = Uris.Companies.makeSpecific(companyId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "name", type = "string"),
            )
        )

        fun deactivateCompany(companyId: Long) = QRreportJsonModel.Action(
            name = "deactivate-company",
            title = "Deactivate company",
            method = HttpMethod.DELETE,
            href = Uris.Companies.makeSpecific(companyId)
        )

        fun activateCompany(companyId: Long) = QRreportJsonModel.Action(
            name = "activate-company",
            title = "Activate company",
            method = HttpMethod.POST,
            href = Uris.Companies.makeSpecific(companyId)
        )
    }

    private fun getCompanyItem(company: CompanyItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.COMPANY),
        rel = rel,
        properties = company,
        links = listOf(Links.self(Uris.Companies.makeSpecific(company.id))),
    )


    fun getCompaniesRepresentation(companiesDto: CompaniesDto, collection: CollectionModel) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMPANY, Classes.COLLECTION),
            properties = collection,
            entities = mutableListOf<QRreportJsonModel>().apply {
                if (companiesDto.companies != null)
                    addAll(companiesDto.companies.map { getCompanyItem(it, listOf(Relations.ITEM)) })
            },
            actions = listOf(Actions.createCompany()),
            links = buildCollectionLinks(collection, COMPANY_PAGE_MAX_SIZE, Uris.Companies.BASE_PATH),
        )
    )

    fun createCompanyRepresentation(company: CompanyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMPANY),
            properties = company,
            links = listOf(Links.self(Uris.Companies.makeSpecific(company.id))),
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Companies.makeSpecific(company.id)),
    )

    fun getCompanyRepresentation(company: CompanyDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMPANY),
            properties = company.removeBuildings(),
            entities = listOf(
                /*getBuildingsRepresentation(
                    company.buildings,
                    company.id,
                    CollectionModel(
                        0,
                        if (BuidingResponses.DEFAULT_PAGE_SIZE < company.buildingsCollectionSize!!)
                            BuidingResponses.DEFAULT_PAGE_SIZE
                        else
                            company.buildingsCollectionSize
                        , company.buildingsCollectionSize),
                    listOf(Relations.COMPANY_BUILDINGS)
                )*/
            ),
            actions = mutableListOf<QRreportJsonModel.Action>().apply {
                if (company.state.compareTo("Inactive") == 0) {
                    add(Actions.activateCompany(company.id))
                    return@apply
                }
                add(Actions.deactivateCompany(company.id))
                add(Actions.updateCompany(company.id))
            },
            links = listOf(
                Links.self(Uris.Companies.makeSpecific(company.id)),
                Links.companies(),
            ),
        )
    )

    fun updateCompanyRepresentation(company: CompanyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMPANY),
            properties = company,
            links = listOf(Links.self(Uris.Companies.makeSpecific(company.id))),
        )
    )

    fun deactivateActivateCompanyRepresentation(company: CompanyItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMPANY),
            properties = company,
            links = listOf(
                Links.self(Uris.Companies.makeSpecific(company.id)),
                Links.companies(),
            ),
        )
    )
}
