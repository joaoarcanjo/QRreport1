package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Person.addRoleToPersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.addSkillToEmployeeAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.assignPersonToCompanyAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.banPersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.createPersonsAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.deleteUserAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.firePersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.getPersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.getPersonsAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.rehirePersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.removeRoleFromPersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.removeSkillFromEmployeeAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.unbanPersonAuthorization
import pt.isel.ps.project.auth.Authorizations.Person.updatePersonAuthorization
import pt.isel.ps.project.model.Uris.Persons
import pt.isel.ps.project.model.person.*
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.PersonResponses.createPersonRepresentation
import pt.isel.ps.project.responses.PersonResponses.deleteUserRepresentation
import pt.isel.ps.project.responses.PersonResponses.fireBanRoleCompanyPersonRepresentation
import pt.isel.ps.project.responses.PersonResponses.getPersonRepresentation
import pt.isel.ps.project.responses.PersonResponses.getPersonsRepresentation
import pt.isel.ps.project.responses.PersonResponses.skillEmployeeRepresentation
import pt.isel.ps.project.responses.PersonResponses.updatePersonRepresentation
import pt.isel.ps.project.service.PersonService
import java.util.*

@RestController
class PersonController(private val service: PersonService) {

    @GetMapping(Persons.BASE_PATH)
    fun getPersons(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        getPersonsAuthorization(user)
        return getPersonsRepresentation(service.getPersons(user, page), page)
    }

    @PostMapping(Persons.BASE_PATH)
    fun createPerson(@RequestBody person: CreatePersonEntity, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        createPersonsAuthorization(user)
        return createPersonRepresentation(service.createPerson(user, person))
    }

    @GetMapping(Persons.SPECIFIC_PATH)
    fun getPerson(@PathVariable personId: UUID, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        getPersonAuthorization(user)
        return getPersonRepresentation(user, service.getPerson(user, personId))
    }

    @PutMapping(Persons.SPECIFIC_PATH)
    fun updatePerson(
        @PathVariable personId: UUID,
        @RequestBody person: UpdatePersonEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        updatePersonAuthorization(user)
        return updatePersonRepresentation(service.updatePerson(user, personId, person))
    }

    @DeleteMapping(Persons.SPECIFIC_PATH)
    fun deleteUser(@PathVariable personId: UUID, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        deleteUserAuthorization(user)
        return deleteUserRepresentation(service.deleteUser(personId))
    }

    @PostMapping(Persons.FIRE_PATH)
    fun firePerson(
        @PathVariable personId: UUID,
        @PathVariable companyId: Long,
        @RequestBody info: FireBanPersonEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        firePersonAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.firePerson(user, personId, companyId, info))
    }

    @PostMapping(Persons.REHIRE_PATH)
    fun rehirePerson(
        @PathVariable personId: UUID,
        @PathVariable companyId: Long,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        rehirePersonAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.rehirePerson(user, personId, companyId))
    }

    @PostMapping(Persons.BAN_PATH)
    fun banPerson(
        @PathVariable personId: UUID,
        @RequestBody info: FireBanPersonEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        banPersonAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.banPerson(user, personId, info))
    }

    @PostMapping(Persons.UNBAN_PATH)
    fun unbanPerson(@PathVariable personId: UUID, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        unbanPersonAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.unbanPerson(user, personId))
    }

    @PutMapping(Persons.ADD_ROLE_PATH)
    fun addRoleToPerson(
        @PathVariable personId: UUID,
        @RequestBody info: AddRoleToPersonEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        addRoleToPersonAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.addRoleToPerson(personId, info))
    }

    @PutMapping(Persons.REMOVE_ROLE_PATH)
    fun removeRoleFromPerson(
        @PathVariable personId: UUID,
        @RequestBody info: RemoveRoleFromPersonEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        removeRoleFromPersonAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.removeRoleFromPerson(personId, info))
    }

    @PutMapping(Persons.ADD_SKILL_PATH)
    fun addSkillToEmployee(
        @PathVariable personId: UUID,
        @RequestBody info: AddRemoveSkillToEmployeeEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        addSkillToEmployeeAuthorization(user)
        return skillEmployeeRepresentation(service.addSkillToEmployee(personId, info))
    }

    @PutMapping(Persons.REMOVE_SKILL_PATH)
    fun removeSkillFromEmployee(
        @PathVariable personId: UUID,
        @RequestBody info: AddRemoveSkillToEmployeeEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        removeSkillFromEmployeeAuthorization(user)
        return skillEmployeeRepresentation(service.removeSkillFromEmployee(personId, info))
    }

    @PostMapping(Persons.ASSIGN_COMPANY_PATH)
    fun assignPersonToCompany(
        @PathVariable personId: UUID,
        @RequestBody info: AssignPersonToCompanyEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        assignPersonToCompanyAuthorization(user)
        return fireBanRoleCompanyPersonRepresentation(service.assignPersonToCompany(personId, info))
    }
}