package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Persons
import pt.isel.ps.project.model.person.*
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
    fun getPersons(): ResponseEntity<QRreportJsonModel> {
        return getPersonsRepresentation(service.getPersons(), 1)
    }

    @PostMapping(Persons.BASE_PATH)
    fun createPerson(@RequestBody person: CreatePersonEntity): ResponseEntity<QRreportJsonModel> {
        return createPersonRepresentation(service.createPerson(person))
    }

    @GetMapping(Persons.SPECIFIC_PATH)
    fun getPerson(@PathVariable personId: UUID): ResponseEntity<QRreportJsonModel> {
        return getPersonRepresentation(service.getPerson(personId))
    }

    @PutMapping(Persons.SPECIFIC_PATH)
    fun updatePerson(@PathVariable personId: UUID, @RequestBody person: UpdatePersonEntity): ResponseEntity<QRreportJsonModel> {
        return updatePersonRepresentation(service.updatePerson(personId, person))
    }

    @DeleteMapping(Persons.SPECIFIC_PATH)
    fun deleteUser(@PathVariable personId: UUID): ResponseEntity<QRreportJsonModel> {
        return deleteUserRepresentation(service.deleteUser(personId))
    }

    @PostMapping(Persons.FIRE_PATH)
    fun firePerson(
        @PathVariable personId: UUID,
        @PathVariable companyId: Long,
        @RequestBody info: FireBanPersonEntity,
    ): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.firePerson(personId, companyId, info))
    }

    @PostMapping(Persons.REHIRE_PATH)
    fun rehirePerson(@PathVariable personId: UUID, @PathVariable companyId: Long): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.rehirePerson(personId, companyId))
    }

    @PostMapping(Persons.BAN_PATH)
    fun banPerson(@PathVariable personId: UUID, @RequestBody info: FireBanPersonEntity): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.banPerson(personId, info))
    }

    @PostMapping(Persons.UNBAN_PATH)
    fun unbanPerson(@PathVariable personId: UUID): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.unbanPerson(personId))
    }

    @PutMapping(Persons.ADD_ROLE_PATH)
    fun addRoleToPerson(@PathVariable personId: UUID, @RequestBody info: AddRoleToPersonEntity): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.addRoleToPerson(personId, info))
    }

    @PutMapping(Persons.REMOVE_ROLE_PATH)
    fun removeRoleFromPerson(@PathVariable personId: UUID, @RequestBody info: RemoveRoleFromPersonEntity): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.removeRoleFromPerson(personId, info))
    }

    @PutMapping(Persons.ADD_SKILL_PATH)
    fun addSkillToEmployee(@PathVariable personId: UUID, @RequestBody info: AddRemoveSkillToEmployeeEntity): ResponseEntity<QRreportJsonModel> {
        return skillEmployeeRepresentation(service.addSkillToEmployee(personId, info))
    }

    @PutMapping(Persons.REMOVE_SKILL_PATH)
    fun removeSkillFromEmployee(@PathVariable personId: UUID, @RequestBody info: AddRemoveSkillToEmployeeEntity): ResponseEntity<QRreportJsonModel> {
        return skillEmployeeRepresentation(service.removeSkillFromEmployee(personId, info))
    }

    @PostMapping(Persons.ASSIGN_COMPANY_PATH)
    fun assignPersonToCompany(@PathVariable personId: UUID, @RequestBody info: AssignPersonToCompanyEntity): ResponseEntity<QRreportJsonModel> {
        return fireBanRoleCompanyPersonRepresentation(service.assignPersonToCompany(personId, info))
    }
}