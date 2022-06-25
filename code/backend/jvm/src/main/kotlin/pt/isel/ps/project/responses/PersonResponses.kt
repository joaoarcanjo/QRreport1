package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Persons
import pt.isel.ps.project.model.person.PersonDetailsDto
import pt.isel.ps.project.model.person.PersonDto
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.TicketResponses.getTicketsRepresentation
import pt.isel.ps.project.util.Validator.Ticket.Person.personHasTwoRoles
import pt.isel.ps.project.util.Validator.Ticket.Person.personIsBanned
import pt.isel.ps.project.util.Validator.Ticket.Person.personIsEmployee
import pt.isel.ps.project.util.Validator.Ticket.Person.personIsInactive
import pt.isel.ps.project.util.Validator.Ticket.Person.personIsManager
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

        fun rehirePerson(personId: UUID, companyId: Long) = QRreportJsonModel.Action(
            name = "rehire-person",
            title = "Rehire person",
            method = HttpMethod.POST,
            href = Persons.makeRehire(companyId, personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number"),
            ),
        )

        fun firePerson(personId: UUID, companyId: Long) = QRreportJsonModel.Action(
            name = "fire-person",
            title = "Fire person",
            method = HttpMethod.POST,
            href = Persons.makeFire(companyId, personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number"),
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
                QRreportJsonModel.Property(name = "skill", type = "number"),
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

    fun getPersonsRepresentation(personsDto: PersonsDto, pageIdx: Int) = Response.buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON, Classes.COLLECTION),
            properties = CollectionModel(pageIdx, PERSON_PAGE_MAX_SIZE, personsDto.personsCollectionSize),
            entities = mutableListOf<QRreportJsonModel>().apply {
                if (personsDto.persons != null)
                    addAll(personsDto.persons.map {
                        getPersonItem(it, listOf(Response.Relations.ITEM))
                    })
            },
            actions = listOf(Actions.createPerson()),
            links = listOf(QRreportJsonModel.Link(
                    listOf(Response.Relations.SELF),
                    Uris.makePagination(pageIdx, Persons.BASE_PATH)
                ),
            ),
        )
    )

    fun createPersonRepresentation(person: PersonDto) = Response.buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Response.Links.self(Persons.makeSpecific(person.id))),
        ),
        HttpStatus.CREATED,
        Response.setLocationHeader(Persons.makeSpecific(person.id)),
    )

    // TODO: Verify logged person role and if it's profile hide some actions as well
    fun getPersonRepresentation(personDetails: PersonDetailsDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = personDetails.person,
            entities = if (personDetails.personTickets != null)
                    listOf(getTicketsRepresentation(personDetails.personTickets, 1))
                else null,
            actions = mutableListOf<QRreportJsonModel.Action>().apply {
                if (personIsBanned(personDetails.person)) {
                    add(Actions.unbanPerson(personDetails.person.id))
                    return@apply
                } else if (personIsInactive(personDetails.person)) {
                    add(Actions.rehirePerson(personDetails.person.id, 1)) // TODO: Put company of logged person
                    return@apply
                }
                if (personIsEmployee(personDetails.person.roles) || personIsManager(personDetails.person.roles)) {
                    add(Actions.firePerson(personDetails.person.id, 1)) // TODO: Put company of logged person
                    add(Actions.assignPersonToCompany(personDetails.person.id))
                }
                if (personDetails.person.roles.containsAll(listOf("employee"))) {
                    add(Actions.addSkill(personDetails.person.id))
                    add(Actions.removeSkill(personDetails.person.id))
                }
                if (personDetails.person.roles.containsAll(listOf("user")))
                    add(Actions.deleteUser(personDetails.person.id))
                add(Actions.banPerson(personDetails.person.id))
                add(Actions.updatePerson(personDetails.person.id))
                add(Actions.addRole(personDetails.person.id))
                if (personHasTwoRoles(personDetails.person.roles))
                    add(Actions.removeRole(personDetails.person.id))
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