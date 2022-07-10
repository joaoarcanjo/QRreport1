package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Persons
import pt.isel.ps.project.model.person.PersonDetailsDto
import pt.isel.ps.project.model.person.PersonDto
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.TicketResponses.getTicketsRepresentation
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Auth.Roles.isUser
import pt.isel.ps.project.util.Validator.Person.employeeHasTwoSkills
import pt.isel.ps.project.util.Validator.Person.isSamePerson
import pt.isel.ps.project.util.Validator.Person.personHasTwoRoles
import pt.isel.ps.project.util.Validator.Person.personIsBanned
import pt.isel.ps.project.util.Validator.Person.personIsEmployee
import pt.isel.ps.project.util.Validator.Person.personIsInactive
import pt.isel.ps.project.util.Validator.Person.personIsManager
import java.util.*

object PersonResponses {
    const val PERSON_PAGE_MAX_SIZE = 10

    object Actions {
        fun createPerson() = QRreportJsonModel.Action(
            name = "create-person",
            title = "Create person",
            method = HttpMethod.POST,
            href = Persons.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "name", type = "string"),
                QRreportJsonModel.Property(name = "phone", type = "string", required = false),
                QRreportJsonModel.Property(name = "email", type = "string"),
                QRreportJsonModel.Property(name = "password", type = "string"),
                QRreportJsonModel.Property(name = "role", type = "string"),
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Companies.BASE_PATH)),
                QRreportJsonModel.Property(name = "skill", type = "number", required = false,
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Categories.BASE_PATH)),
            ),
        )

        fun updatePerson(personId: UUID) = QRreportJsonModel.Action(
            name = "update-person",
            title = "Update person",
            method = HttpMethod.PUT,
            href = Persons.makeSpecific(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "name", type = "string", required = false),
                QRreportJsonModel.Property(name = "phone", type = "string", required = false),
                QRreportJsonModel.Property(name = "email", type = "string", required = false),
                QRreportJsonModel.Property(name = "password", type = "string", required = false),
            ),
        )

        fun unbanPerson(personId: UUID) = QRreportJsonModel.Action(
            name = "unban-person",
            title = "Unban person",
            method = HttpMethod.POST,
            href = Persons.makeUnban(personId),
        )

        fun banPerson(personId: UUID) = QRreportJsonModel.Action(
            name = "ban-person",
            title = "Ban person",
            method = HttpMethod.POST,
            href = Persons.makeBan(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "reason", type = "string"),
            )
        )

        fun rehirePerson(personId: UUID) = QRreportJsonModel.Action(
            name = "rehire-person",
            title = "Rehire person",
            method = HttpMethod.POST,
            href = Persons.makeRehire(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Companies.BASE_PATH)),
            ),      // TODO: Path to get only the auth person companies
        )

        fun firePerson(personId: UUID) = QRreportJsonModel.Action(
            name = "fire-person",
            title = "Fire person",
            method = HttpMethod.POST,
            href = Persons.makeFire(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Companies.BASE_PATH)),
                    // TODO: Path to get only the auth person companies
                QRreportJsonModel.Property(name = "reason", type = "string"),
            ),
        )

        fun deleteUser(personId: UUID) = QRreportJsonModel.Action(
            name = "delete-user",
            title = "Delete user",
            method = HttpMethod.DELETE,
            href = Persons.makeSpecific(personId),
        )

        fun addRole(personId: UUID) = QRreportJsonModel.Action(
            name = "add-role",
            title = "Add role",
            method = HttpMethod.PUT,
            href = Persons.makeAddRole(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "role", type = "string"),
                QRreportJsonModel.Property(name = "company", type = "number", required = false,
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Companies.BASE_PATH)),
                QRreportJsonModel.Property(name = "skill", type = "number", required = false,
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Categories.BASE_PATH)),
            ),
        )

        fun removeRole(personId: UUID) = QRreportJsonModel.Action(
            name = "remove-role",
            title = "Remove role",
            method = HttpMethod.PUT,
            href = Persons.makeRemoveRole(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "role", type = "string"),
            ),
        )

        fun addSkill(personId: UUID) = QRreportJsonModel.Action(
            name = "add-skill",
            title = "Add skill",
            method = HttpMethod.PUT,
            href = Persons.makeAddSkill(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "skill", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Categories.BASE_PATH)),
            ),
        )

        fun removeSkill(personId: UUID) = QRreportJsonModel.Action(
            name = "remove-skill",
            title = "Remove skill",
            method = HttpMethod.PUT,
            href = Persons.makeRemoveSkill(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "skill", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Categories.BASE_PATH)),
            ),
        )

        fun assignPersonToCompany(personId: UUID) = QRreportJsonModel.Action(
            name = "assign-to-company",
            title = "Assign to company",
            method = HttpMethod.PUT,
            href = Persons.makeAssignCompany(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Companies.BASE_PATH)),
            ),
        )
    }

    fun getPersonItem(person: PersonItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.PERSON),
        rel = rel,
        properties = person,
        links = listOf(Response.Links.self(Persons.makeSpecific(person.id))),
    )

    fun getPersonsRepresentation(personsDto: PersonsDto, pageIdx: Int) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON, Classes.COLLECTION),
            properties = CollectionModel(pageIdx, PERSON_PAGE_MAX_SIZE, personsDto.personsCollectionSize),
            entities = mutableListOf<QRreportJsonModel>().apply {
                if (personsDto.persons != null)
                    addAll(personsDto.persons.map {
                        getPersonItem(it, listOf(Relations.ITEM))
                    })
            },
            actions = listOf(Actions.createPerson()),
            links = listOf(
                QRreportJsonModel.Link(listOf(Relations.SELF), Uris.makePagination(pageIdx, Persons.BASE_PATH)),
                QRreportJsonModel.Link(listOf(Relations.PAGINATION), Persons.PERSONS_PAGINATION, templated = true)
            ),
        )
    )

    fun createPersonRepresentation(person: PersonDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Response.Links.self(Persons.makeSpecific(person.id))),
        ),
        HttpStatus.CREATED,
        Response.setLocationHeader(Persons.makeSpecific(person.id)),
    )

    fun getPersonRepresentation(user: AuthPerson, personDetails: PersonDetailsDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = personDetails.person,
            entities = if (personDetails.personTickets != null)
                    listOf(getTicketsRepresentation(personDetails.personTickets, 1))
                else null,
            actions = mutableListOf<QRreportJsonModel.Action>().apply {
                // Delete
                if (personDetails.person.roles.containsAll(listOf("user")))
                    add(Actions.deleteUser(personDetails.person.id))
                // Update
                if (isSamePerson(user, personDetails.person.id)) {
                    add(Actions.updatePerson(personDetails.person.id))
                    if (!isManager(user) || !isAdmin(user)) return@apply
                }
                // Ban
                add(if (personIsBanned(personDetails.person)) Actions.unbanPerson(personDetails.person.id)
                    else Actions.banPerson(personDetails.person.id))
                // Fire
                if (personIsEmployee(personDetails.person.roles) || personIsManager(personDetails.person.roles)) {
                    if (personIsInactive(personDetails.person)) {
                        add(Actions.rehirePerson(personDetails.person.id))
                    } else {
                        add(Actions.firePerson(personDetails.person.id))
                        add(Actions.assignPersonToCompany(personDetails.person.id))
                    }
                }
                // Roles and skills
                if (isAdmin(user)) {
                    if (personDetails.person.roles.containsAll(listOf("employee"))) {
                        add(Actions.addSkill(personDetails.person.id))
                        if (employeeHasTwoSkills(personDetails.person.skills!!))
                            add(Actions.removeSkill(personDetails.person.id))
                    }
                    add(Actions.addRole(personDetails.person.id))
                    if (personHasTwoRoles(personDetails.person.roles))
                        add(Actions.removeRole(personDetails.person.id))
                }
            },
            links = listOf(
                Response.Links.self(Persons.makeSpecific(personDetails.person.id)),
            )
        )
    )

    fun updatePersonRepresentation(person: PersonItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Response.Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun deleteUserRepresentation(person: PersonDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Response.Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun fireBanRoleCompanyPersonRepresentation(person: PersonDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Response.Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun skillEmployeeRepresentation(person: PersonItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Response.Links.self(Persons.makeSpecific(person.id)))
        )
    )
}