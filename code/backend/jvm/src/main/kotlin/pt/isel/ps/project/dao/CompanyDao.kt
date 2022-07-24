package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transaction
import pt.isel.ps.project.model.company.COMPANY_REP
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.responses.CompanyResponses.COMPANY_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.PersonResponses
import java.util.*

interface CompanyDao {

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_companies(:userId, $COMPANY_PAGE_MAX_SIZE, :skip);")
    fun getCompanies(userId: UUID?, skip: Int): String

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_user_companies(:userId, :isManager, :filteredUser, :state, $COMPANY_PAGE_MAX_SIZE, :skip);")
    fun getUserCompanies(userId: UUID?, isManager: Boolean, filteredUser: UUID, state: String, skip: Int): String

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_new_companies(:userId, $COMPANY_PAGE_MAX_SIZE, :skip);")
    fun getNewCompanies(userId: UUID, skip: Int): String

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlCall("CALL create_company(:$COMPANY_REP, :name);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun createCompany(@BindBean company: CreateCompanyEntity): OutParameters

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_company(:companyId, $COMPANY_PAGE_MAX_SIZE, 0);")
    fun getCompany(companyId: Long): String

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlCall("CALL update_company(:$COMPANY_REP, :companyId, :name);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun updateCompany(companyId: Long, @BindBean company: UpdateCompanyEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlCall("CALL deactivate_company(:$COMPANY_REP, :companyId);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateCompany(companyId: Long): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlCall("CALL activate_company(:$COMPANY_REP, :companyId);")
    @OutParameter(name = COMPANY_REP, sqlType = java.sql.Types.OTHER)
    fun activateCompany(companyId: Long): OutParameters
}
