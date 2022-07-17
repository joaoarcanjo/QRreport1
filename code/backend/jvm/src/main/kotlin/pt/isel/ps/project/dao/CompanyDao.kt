package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.company.COMPANY_REP
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.responses.CompanyResponses.COMPANY_PAGE_MAX_SIZE
import java.util.*

interface CompanyDao {

    @SqlQuery("SELECT get_companies(:userId, $COMPANY_PAGE_MAX_SIZE, :skip);")
    fun getCompanies(userId: UUID?, skip: Int): String

    @SqlCall("CALL create_company(:$COMPANY_REP, :name);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun createCompany(@BindBean company: CreateCompanyEntity): OutParameters

    @SqlQuery("SELECT get_company(:companyId, $COMPANY_PAGE_MAX_SIZE, 0);")
    fun getCompany(companyId: Long): String

    @SqlCall("CALL update_company(:$COMPANY_REP, :companyId, :name);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun updateCompany(companyId: Long, @BindBean company: UpdateCompanyEntity): OutParameters

    @SqlCall("CALL deactivate_company(:$COMPANY_REP, :companyId);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateCompany(companyId: Long): OutParameters

    @SqlCall("CALL activate_company(:$COMPANY_REP, :companyId);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun activateCompany(companyId: Long): OutParameters
}
