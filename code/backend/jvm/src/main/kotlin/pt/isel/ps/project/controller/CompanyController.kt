package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Companies
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.model.representations.CollectionModel
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
    fun getCompanies(): ResponseEntity<QRreportJsonModel> {
        val companies = service.getCompanies()
        return getCompaniesRepresentation(
            companies,
            CollectionModel(1, COMPANY_PAGE_MAX_SIZE, companies.companiesCollectionSize)
        )
    }

    @PostMapping(Companies.BASE_PATH)
    fun createCompany(@RequestBody company: CreateCompanyEntity): ResponseEntity<QRreportJsonModel> {
        return createCompanyRepresentation(service.createCompany(company))
    }

    @GetMapping(Companies.SPECIFIC_PATH)
    fun getCompany(@PathVariable companyId: Long): ResponseEntity<QRreportJsonModel> {
        return getCompanyRepresentation(service.getCompany(companyId))
    }

    @PutMapping(Companies.SPECIFIC_PATH)
    fun updateCompany(@PathVariable companyId: Long, @RequestBody company: UpdateCompanyEntity): ResponseEntity<QRreportJsonModel> {
        return updateCompanyRepresentation(service.updateCompany(companyId, company))
    }

    @PostMapping(Companies.DEACTIVATE_PATH)
    fun deactivateCompany(@PathVariable companyId: Long): ResponseEntity<QRreportJsonModel> {
        return deactivateActivateCompanyRepresentation(service.deactivateCompany(companyId))
    }

    @PostMapping(Companies.ACTIVATE_PATH)
    fun activateCompany(@PathVariable companyId: Long): ResponseEntity<QRreportJsonModel> {
        return deactivateActivateCompanyRepresentation(service.activateCompany(companyId))
    }
}
