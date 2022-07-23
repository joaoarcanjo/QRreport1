package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Companies.companiesSelf
import pt.isel.ps.project.model.Uris.Persons
import pt.isel.ps.project.model.Uris.Persons.PERSONS_PAGINATION
import pt.isel.ps.project.model.Uris.Persons.personsPagination
import pt.isel.ps.project.model.Uris.Persons.personsSelf
import pt.isel.ps.project.model.person.PersonDetailsDto
import pt.isel.ps.project.model.person.PersonDto
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_DIRECTION
import pt.isel.ps.project.model.representations.DEFAULT_SORT
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.state.States.ACTIVE
import pt.isel.ps.project.model.state.States.INACTIVE
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.TicketResponses.getTicketsRepresentation
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Person.employeeHasTwoSkills
import pt.isel.ps.project.util.Validator.Person.isSamePerson
import pt.isel.ps.project.util.Validator.Person.personHasTwoRoles
import pt.isel.ps.project.util.Validator.Person.personIsBanned
import pt.isel.ps.project.util.Validator.Person.personIsEmployee
import pt.isel.ps.project.util.Validator.Person.personIsGuest
import pt.isel.ps.project.util.Validator.Person.personIsInactive
import pt.isel.ps.project.util.Validator.Person.personIsManager
import pt.isel.ps.project.util.Validator.Person.personIsUser
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
            templated = true,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(companiesSelf(1, personId, INACTIVE, false))),
                ),
        )

        fun firePerson(personId: UUID) = QRreportJsonModel.Action(
            name = "fire-person",
            title = "Fire person",
            method = HttpMethod.POST,
            href = Persons.makeFire(personId),
            templated = true,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(companiesSelf(1, personId, ACTIVE, false))),
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
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(companiesSelf(1, personId, ACTIVE, false))),
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
            method = HttpMethod.POST,
            href = Persons.makeAssignCompany(personId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "company", type = "number",
                    possibleValues = QRreportJsonModel.PropertyValue(companiesSelf(1, personId, ACTIVE, true))),
            ),
        )

        fun switchRole() = QRreportJsonModel.Action(
            name = "switch-role",
            title = "Switch role",
            method = HttpMethod.POST,
            href = Persons.SWITCH_ROLE,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(QRreportJsonModel.Property(name = "role", type = "string")),
        )
    }

    fun getPersonItem(person: PersonItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.PERSON),
        rel = rel,
        properties = person,
        links = listOf(Links.self(Persons.makeSpecific(person.id))),
    )

    fun getPersonsRepresentation(personsDto: PersonsDto, pageIdx: Int, company: Long?, role: String) = buildResponse(
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
                Links.self(personsSelf(pageIdx, company, role)),
                Links.pagination(personsPagination(company, role)),
            ),
        )
    )

    fun createPersonRepresentation(person: PersonDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Links.self(Persons.makeSpecific(person.id))),
        ),
        HttpStatus.CREATED,
        Response.setLocationHeader(Persons.makeSpecific(person.id)),
    )

    fun getPersonRepresentation(user: AuthPerson, personDetails: PersonDetailsDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = personDetails.person,
            entities = if (personDetails.personTickets != null)
                    listOf(getTicketsRepresentation(personDetails.personTickets, null, null, DEFAULT_DIRECTION, DEFAULT_SORT, null, 1))
                else null,
            actions = mutableListOf<QRreportJsonModel.Action>().apply {
                if (isAdmin(user) || isManager(user)) {
                    // Fire/Rehire
                    if (!isSamePerson(user, personDetails.person.id) &&
                        (personIsEmployee(personDetails.person.roles) || personIsManager(personDetails.person.roles))) {
                        add(Actions.rehirePerson(personDetails.person.id))
                        add(Actions.firePerson(personDetails.person.id))

                        //Assign Person
                        if (!personIsInactive(personDetails.person) && isAdmin(user)) {
                            add(Actions.assignPersonToCompany(personDetails.person.id))
                        }
                    // Ban/Unban
                    } else if (!isSamePerson(user, personDetails.person.id)
                        && (personIsGuest(personDetails.person.roles) || personIsUser(personDetails.person.roles))) {

                        if (personIsBanned(personDetails.person)) {
                            add(Actions.unbanPerson(personDetails.person.id))
                            return@apply
                        } else add(Actions.banPerson(personDetails.person.id))
                    }
                }

                // Delete
                if (!isSamePerson(user, personDetails.person.id) && personDetails.person.roles.containsAll(listOf("user")))
                    add(Actions.deleteUser(personDetails.person.id))
                // Update
                if (isSamePerson(user, personDetails.person.id))
                    add(Actions.updatePerson(personDetails.person.id))
                if (personDetails.person.roles.size > 1)
                    add(Actions.switchRole())

                if (!isManager(user) && !isAdmin(user)) return@apply

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
                Links.self(Persons.makeSpecific(personDetails.person.id)),
            )
        )
    )

    fun updatePersonRepresentation(person: PersonItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun deleteUserRepresentation(person: PersonDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun fireBanRoleCompanyPersonRepresentation(person: PersonDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun skillEmployeeRepresentation(person: PersonItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = listOf(Links.self(Persons.makeSpecific(person.id)))
        )
    )

    fun switchRoleRepresentation(person: PersonDto, role: String) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON),
            properties = person,
            links = mutableListOf<QRreportJsonModel.Link>().apply {
                add(Links.self(Persons.makeSpecific(person.id)))
                if (role == "manager" || role == "admin") {
                    add(Links.companies())
                    add(Links.persons())
                }
                if (role == "admin") {
                    add(Links.devices())
                    add(Links.categories())
                }
                add(Links.tickets())
            }
        )
    )
}