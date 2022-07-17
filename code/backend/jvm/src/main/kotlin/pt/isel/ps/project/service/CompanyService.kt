package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.CompanyDao
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.responses.CompanyResponses.COMPANY_PAGE_MAX_SIZE
import pt.isel.ps.project.util.Validator.Company.personBelongsToCompany
import pt.isel.ps.project.util.Validator.Company.verifyCreateCompanyInput
import pt.isel.ps.project.util.Validator.Company.verifyUpdateCompanyInput
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class CompanyService(private val companyDao: CompanyDao) {

    fun getCompanies(user: AuthPerson, page: Int): CompaniesDto {
        // If he's a manager, get only the companies that he belongs
        val userId = if (isManager(user)) user.id else null
        return companyDao.getCompanies(userId, elemsToSkip(page, COMPANY_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun createCompany(company: CreateCompanyEntity): CompanyItemDto {
        verifyCreateCompanyInput(company)
        val companyDto = companyDao.createCompany(company).getString(COMPANY_REP)?.deserializeJsonTo<CompanyItemDto>()
        return companyDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getCompany(companyId: Long, user: AuthPerson, page: Int): CompanyDto {
        // If he's a manager, verify if belongs to the company
        if (isManager(user) && !personBelongsToCompany(user, companyId)) throw ForbiddenException(ACCESS_DENIED)
        return companyDao.getCompany(companyId, elemsToSkip(page, COMPANY_PAGE_MAX_SIZE)).deserializeJsonTo()
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
