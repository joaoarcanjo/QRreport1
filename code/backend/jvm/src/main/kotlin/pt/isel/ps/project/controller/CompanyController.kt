package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Companies
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.service.CompanyService

@RestController
class CompanyController(private val service: CompanyService) {

    @GetMapping(Companies.BASE_PATH)
    fun getCompanies(): CompaniesDto {
        return service.getCompanies()
    }

    @PostMapping(Companies.BASE_PATH)
    fun createCompany(@RequestBody company: CreateCompanyEntity): CompanyItemDto {
        return service.createCompany(company)
    }

    @GetMapping(Companies.SPECIFIC_PATH)
    fun getCompany(@PathVariable companyId: Long): CompanyDto {
        return service.getCompany(companyId)
    }

    @PutMapping(Companies.SPECIFIC_PATH)
    fun updateCompany(@PathVariable companyId: Long, @RequestBody company: UpdateCompanyEntity): CompanyItemDto {
        company.id = companyId
        return service.updateCompany(company)
    }

    @DeleteMapping(Companies.SPECIFIC_PATH)
    fun deactivateCompany(@PathVariable companyId: Long): CompanyItemDto {
        return service.deactivateCompany(companyId)
    }

    @PutMapping(Companies.ACTIVATE_PATH)
    fun activateCompany(@PathVariable companyId: Long): CompanyItemDto {
        return service.activateCompany(companyId)
    }
}