package pt.isel.ps.project.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.PersonDao
import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CHANGE_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.UPDATE_PERSON
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.Errors.PersonBan.Message.WRONG_PERSON_BAN
import pt.isel.ps.project.exception.Errors.PersonDismissal.Message.WRONG_PERSON_DISMISSAL
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.exception.PersonBanException
import pt.isel.ps.project.exception.PersonDismissalException
import pt.isel.ps.project.model.Uris.UNDEFINED
import pt.isel.ps.project.model.Uris.UNDEFINED_ID
import pt.isel.ps.project.model.person.*
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.responses.PersonResponses.PERSON_PAGE_MAX_SIZE
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Person.isSamePerson
import pt.isel.ps.project.util.Validator.Person.belongsToCompany
import pt.isel.ps.project.util.Validator.Person.verifyAddRoleInput
import pt.isel.ps.project.util.Validator.Person.verifyCreatePersonInput
import pt.isel.ps.project.util.Validator.Person.verifyManagerCreationPermissions
import pt.isel.ps.project.util.Validator.Person.verifyUpdatePersonInput
import pt.isel.ps.project.util.deserializeJsonTo
import java.util.*

@Service
class PersonService(private val personDao: PersonDao, private val passwordEncoder: PasswordEncoder) {

    fun getPersons(user: AuthPerson, companyId: Long?, role: String, page: Int): PersonsDto {
        // Managers can only get his employees (i.e. same company)
        // Admins can get everyone
        if (companyId != null && role != UNDEFINED)
            return personDao.getCompanyPersons(user.id, companyId, role, elemsToSkip(page, PERSON_PAGE_MAX_SIZE)).deserializeJsonTo()
        return personDao.getPersons(user.id, isManager(user), elemsToSkip(page, PERSON_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun createPerson(user: AuthPerson, person: CreatePersonEntity): PersonDto {
        verifyCreatePersonInput(person)
        // Managers can only create other managers or employees
        if (isManager(user)) verifyManagerCreationPermissions(user, person)
        val personDto = personDao.createPerson(person).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getPerson(user: AuthPerson, reqPersonId: UUID): PersonDetailsDto {
        if (!isManager(user) && !isAdmin(user) && !isSamePerson(user, reqPersonId)) throw ForbiddenException(ACCESS_DENIED)
        return personDao.getPerson(user.id, reqPersonId).deserializeJsonTo()
    }

    fun updatePerson(user: AuthPerson, personId: UUID, person: UpdatePersonEntity): PersonItemDto {
        verifyUpdatePersonInput(person)
        if (!isAdmin(user) && !isSamePerson(user, personId)) throw ForbiddenException(ACCESS_DENIED, UPDATE_PERSON)
        val encPassword = if (person.password != null) passwordEncoder.encode(person.password) else null
        val personDto = personDao.updatePerson(personId, person, encPassword).getString(PERSON_REP)?.deserializeJsonTo<PersonItemDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deleteUser(personId: UUID): PersonDto {
        val personDto = personDao.deleteUser(personId).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun firePerson(user: AuthPerson, personId: UUID, companyId: Long, info: FireBanPersonEntity): PersonDto {
        if (isManager(user) && (isSamePerson(user, personId) || !belongsToCompany(user, companyId)))
            throw PersonDismissalException(WRONG_PERSON_DISMISSAL)
        val personDto = personDao.firePerson(personId, companyId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun rehirePerson(user: AuthPerson, personId: UUID, companyId: Long): PersonDto {
        if (isManager(user) && (isSamePerson(user, personId) || !belongsToCompany(user, companyId)))
            throw PersonDismissalException(WRONG_PERSON_DISMISSAL)
        val personDto = personDao.rehirePerson(personId, companyId).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun banPerson(user: AuthPerson, personId: UUID, info: FireBanPersonEntity): PersonDto {
        if (isSamePerson(user, personId)) throw PersonBanException(WRONG_PERSON_BAN)
        val personDto = personDao.banPerson(user.id, personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun unbanPerson(user: AuthPerson, personId: UUID): PersonDto {
        if (isSamePerson(user, personId)) throw PersonBanException(WRONG_PERSON_BAN)
        val personDto = personDao.unbanPerson(user.id, personId).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun addRoleToPerson(personId: UUID, info: AddRoleToPersonEntity): PersonDto {
        verifyAddRoleInput(info)
        val personDto = personDao.addRoleToPerson(personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun removeRoleFromPerson(personId: UUID, info: RemoveRoleFromPersonEntity): PersonDto {
        val personDto = personDao.removeRoleFromPerson(personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun addSkillToEmployee(personId: UUID, info: AddRemoveSkillToEmployeeEntity): PersonItemDto {
        val personDto = personDao.addSkillToEmployee(personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonItemDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun removeSkillFromEmployee(personId: UUID, info: AddRemoveSkillToEmployeeEntity): PersonItemDto {
        val personDto = personDao.removeSkillFromEmployee(personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonItemDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun assignPersonToCompany(personId: UUID, info: AssignPersonToCompanyEntity): PersonDto {
        val personDto = personDao.assignPersonToCompany(personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun switchRole(role: SwitchRoleEntity, user: AuthPerson): PersonDto {
        val personDto = personDao.switchRole(user.id, role).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getPossibleRoles(user: AuthPerson):RolesDto {
        return personDao.getPossibleRoles(isManager(user)).deserializeJsonTo()
    }
}