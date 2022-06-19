package pt.isel.ps.project.service

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.CompanyDao
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.util.Validator.Company.verifyCreateCompanyInput
import pt.isel.ps.project.util.Validator.Company.verifyUpdateCompanyInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class CompanyService(jdbi: Jdbi) {

    private val companyDao = jdbi.onDemand<CompanyDao>()

    fun getCompanies(): CompaniesDto {
        return companyDao.getCompanies().deserializeJsonTo()
    }

    fun createCompany(company: CreateCompanyEntity): CompanyItemDto {
        verifyCreateCompanyInput(company)
        val companyDto = companyDao.createCompany(company).getString(COMPANY_REP)?.deserializeJsonTo<CompanyItemDto>()
        return companyDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getCompany(companyId: Long): CompanyDto {
        return companyDao.getCompany(companyId).deserializeJsonTo()
    }

    fun updateCompany(companyId: Long, company: UpdateCompanyEntity): CompanyItemDto {
        verifyUpdateCompanyInput(company)
        val companyDto = companyDao.updateCompany(companyId, company).getString(COMPANY_REP)?.deserializeJsonTo<CompanyItemDto>()
        return companyDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deactivateCompany(companyId: Long): CompanyItemDto {
        val companyDto = companyDao.deactivateCompany(companyId).getString(COMPANY_REP)?.deserializeJsonTo<CompanyItemDto>()
        return companyDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun activateCompany(companyId: Long): CompanyItemDto {
        val companyDto = companyDao.activateCompany(companyId).getString(COMPANY_REP)?.deserializeJsonTo<CompanyItemDto>()
        return companyDto ?: throw InternalServerException(INTERNAL_ERROR)
    }
}
