package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes

object PersonResponses {
    const val PERSON_PAGE_MAX_SIZE = 10

    object Actions {
        fun createPerson() = QRreportJsonModel.Action(
            name = "create-person",
            title = "Create a person",
            method = HttpMethod.POST,
            href = Uris.Persons.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property(name = "name", type = "string"),
                QRreportJsonModel.Property(name = "phone", type = "string", required = false),
                QRreportJsonModel.Property(name = "email", type = "string"),
                QRreportJsonModel.Property(name = "role", type = "string"),
                // Password only inserted in create account
            )
        )
    }

    fun getPersonItem(person: PersonItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.PERSON),
        rel = rel,
        properties = person,
        links = listOf(Response.Links.self(Uris.Persons.makeSpecific(person.id))),
    )

    fun getPersonsRepresentation(personsDto: PersonsDto, collection: CollectionModel) = Response.buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.PERSON, Classes.COLLECTION),
            properties = collection,
            entities = mutableListOf<QRreportJsonModel>().apply {
                if (personsDto.persons != null)
                    addAll(personsDto.persons.map {
                        getPersonItem(it, listOf(Response.Relations.ITEM))
                    })
            },
            actions = listOf(Actions.createPerson()),
            links = Response.buildCollectionLinks(
                collection,
                PERSON_PAGE_MAX_SIZE,
                Uris.Persons.BASE_PATH
            ),
        )
    )
}