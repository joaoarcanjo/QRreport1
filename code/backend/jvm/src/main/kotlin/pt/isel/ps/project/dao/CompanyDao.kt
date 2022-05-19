package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.company.COMPANY_REP
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity

interface CompanyDao {

    @SqlQuery("SELECT get_companies(null, null);") // :limit, :offset
    fun getCompanies(): String

    @SqlCall("CALL create_company(:name, :$COMPANY_REP);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun createCompany(@BindBean company: CreateCompanyEntity): OutParameters

    @SqlQuery("SELECT get_company(:companyId);")
    fun getCompany(companyId: Long): String

    @SqlCall("CALL update_company(:id, :$COMPANY_REP, :name);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun updateCompany(@BindBean company: UpdateCompanyEntity): OutParameters

    @SqlCall("CALL deactivate_company(:companyId, :$COMPANY_REP);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateCompany(companyId: Long): OutParameters

    @SqlCall("CALL activate_company(:companyId, :$COMPANY_REP);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun activateCompany(companyId: Long): OutParameters
}