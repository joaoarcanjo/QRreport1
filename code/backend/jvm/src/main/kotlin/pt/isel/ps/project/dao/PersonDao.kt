package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transaction
import pt.isel.ps.project.model.person.*
import pt.isel.ps.project.responses.PersonResponses.PERSON_PAGE_MAX_SIZE
import java.util.UUID

interface PersonDao {
    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlQuery("SELECT get_persons(:managerId, :isManager, $PERSON_PAGE_MAX_SIZE, :skip);")
    fun getPersons(managerId: UUID?, isManager: Boolean, skip: Int): String

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlQuery("SELECT get_company_persons(:userId, :companyId, :role, $PERSON_PAGE_MAX_SIZE, :skip);")
    fun getCompanyPersons(userId: UUID?, companyId: Long, role: String, skip: Int): String

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL create_person(:$PERSON_REP, :role, :name, :email, :encPass, :phone, :company, :skill);")
    fun createPerson(@BindBean person: CreatePersonEntity, encPass: String): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlQuery("SELECT get_person(:reqPersonId, :personId);")
    fun getPerson(reqPersonId: UUID, personId: UUID): String

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL update_person(:$PERSON_REP, :personId, :name, :phone, :email, :encPassword);")
    fun updatePerson(personId: UUID, @BindBean person: UpdatePersonEntity, encPassword: String?): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL delete_user(:$PERSON_REP, :personId);")
    fun deleteUser(personId: UUID): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL fire_person(:$PERSON_REP, :personId, :companyId, :reason);")
    fun firePerson(personId: UUID, companyId: Long, @BindBean info: FireBanPersonEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL rehire_person(:$PERSON_REP, :personId, :companyId);")
    fun rehirePerson(personId: UUID, companyId: Long): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL ban_person(:$PERSON_REP, :reqPersonId, :personId, :reason);")
    fun banPerson(reqPersonId: UUID, personId: UUID, @BindBean info: FireBanPersonEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL unban_person(:$PERSON_REP, :reqPersonId, :personId);")
    fun unbanPerson(reqPersonId: UUID, personId: UUID): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL add_role_to_person(:$PERSON_REP, :personId, :role, :company, :skill);")
    fun addRoleToPerson(personId: UUID, @BindBean info: AddRoleToPersonEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL remove_role_from_person(:$PERSON_REP, :personId, :role);")
    fun removeRoleFromPerson(personId: UUID, @BindBean info: RemoveRoleFromPersonEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL add_skill_to_employee(:$PERSON_REP, :personId, :skill);")
    fun addSkillToEmployee(personId: UUID, @BindBean info: AddRemoveSkillToEmployeeEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL remove_skill_from_employee(:$PERSON_REP, :personId, :skill);")
    fun removeSkillFromEmployee(personId: UUID, @BindBean info: AddRemoveSkillToEmployeeEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL assign_person_to_company(:$PERSON_REP, :personId, :company);")
    fun assignPersonToCompany(personId: UUID, @BindBean info: AssignPersonToCompanyEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = PERSON_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL switch_role(:$PERSON_REP, :personId, :role);")
    fun switchRole(personId: UUID, @BindBean roleEntity: SwitchRoleEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlQuery("SELECT get_possible_roles(:isManager);")
    fun getPossibleRoles(isManager: Boolean): String
}
