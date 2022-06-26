package pt.isel.ps.project.service

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.PersonDao
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.person.*
import pt.isel.ps.project.util.Validator.Ticket.Person.verifyCreatePersonInput
import pt.isel.ps.project.util.Validator.Ticket.Person.verifyUpdatePersonInput
import pt.isel.ps.project.util.deserializeJsonTo
import java.util.*

@Service
class PersonService(jdbi: Jdbi, private val passwordEncoder: PasswordEncoder) {

    private val personDao = jdbi.onDemand<PersonDao>()

    fun getPersons(): PersonsDto {
        return personDao.getPersons().deserializeJsonTo()
    }

    fun createPerson(person: CreatePersonEntity): PersonDto {
        verifyCreatePersonInput(person)
        val personDto = personDao.createPerson(person).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun getPerson(reqPersonId: UUID): PersonDetailsDto {
        return personDao.getPerson(reqPersonId).deserializeJsonTo()
    }

    fun updatePerson(personId: UUID, person: UpdatePersonEntity): PersonItemDto {
        verifyUpdatePersonInput(person)
        val encPassword = if (person.password != null) passwordEncoder.encode(person.password) else null
        val personDto = personDao.updatePerson(personId, person, encPassword).getString(PERSON_REP)?.deserializeJsonTo<PersonItemDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deleteUser(personId: UUID): PersonDto {
        val personDto = personDao.deleteUser(personId).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun firePerson(personId: UUID, companyId: Long, info: FireBanPersonEntity): PersonDto {
        val personDto = personDao.firePerson(personId, companyId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun rehirePerson(personId: UUID, companyId: Long): PersonDto {
        val personDto = personDao.rehirePerson(personId, companyId).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun banPerson(personId: UUID, info: FireBanPersonEntity): PersonDto {
        val personDto = personDao.banPerson(personId, info).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun unbanPerson(personId: UUID): PersonDto {
        val personDto = personDao.unbanPerson(personId).getString(PERSON_REP)?.deserializeJsonTo<PersonDto>()
        return personDto ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun addRoleToPerson(personId: UUID, info: AddRoleToPersonEntity): PersonDto {
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
}