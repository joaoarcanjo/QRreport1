package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Company.activateCompanyAuthorization
import pt.isel.ps.project.auth.Authorizations.Company.createCompanyAuthorization
import pt.isel.ps.project.auth.Authorizations.Company.deactivateCompanyAuthorization
import pt.isel.ps.project.auth.Authorizations.Company.getCompaniesAuthorization
import pt.isel.ps.project.auth.Authorizations.Company.getCompanyAuthorization
import pt.isel.ps.project.auth.Authorizations.Company.updateCompanyAuthorization
import pt.isel.ps.project.model.Uris.Companies
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.CompanyResponses.COMPANY_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.CompanyResponses.createCompanyRepresentation
import pt.isel.ps.project.responses.CompanyResponses.deactivateActivateCompanyRepresentation
import pt.isel.ps.project.responses.CompanyResponses.getCompaniesRepresentation
import pt.isel.ps.project.responses.CompanyResponses.getCompanyRepresentation
import pt.isel.ps.project.responses.CompanyResponses.updateCompanyRepresentation
import pt.isel.ps.project.service.CompanyService

@RestController
class CompanyController(private val service: CompanyService) {

    @GetMapping(Companies.BASE_PATH)
    fun getCompanies(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        getCompaniesAuthorization(user)
        val companies = service.getCompanies(user, page)
        return getCompaniesRepresentation(
            user,
            companies,
            CollectionModel(page, COMPANY_PAGE_MAX_SIZE, companies.companiesCollectionSize)
        )
    }

    @PostMapping(Companies.BASE_PATH)
    fun createCompany(@RequestBody company: CreateCompanyEntity, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        createCompanyAuthorization(user)
        return createCompanyRepresentation(service.createCompany(company))
    }

    @GetMapping(Companies.SPECIFIC_PATH)
    fun getCompany(@PathVariable companyId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        getCompanyAuthorization(user)
        return getCompanyRepresentation(user, service.getCompany(companyId, user))
    }

    @PutMapping(Companies.SPECIFIC_PATH)
    fun updateCompany(
        @PathVariable companyId: Long,
        @RequestBody company: UpdateCompanyEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        updateCompanyAuthorization(user)
        return updateCompanyRepresentation(service.updateCompany(companyId, company))
    }

    @PostMapping(Companies.DEACTIVATE_PATH)
    fun deactivateCompany(@PathVariable companyId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        deactivateCompanyAuthorization(user)
        return deactivateActivateCompanyRepresentation(service.deactivateCompany(companyId))
    }

    @PostMapping(Companies.ACTIVATE_PATH)
    fun activateCompany(@PathVariable companyId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        activateCompanyAuthorization(user)
        return deactivateActivateCompanyRepresentation(service.activateCompany(companyId))
    }
}
