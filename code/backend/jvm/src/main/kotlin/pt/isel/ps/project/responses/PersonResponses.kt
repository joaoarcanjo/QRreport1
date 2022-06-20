package pt.isel.ps.project.responses

import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links

object PersonResponses {

    fun getPersonItem (person: PersonItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.PERSON),
        rel = rel,
        properties = person,
        links = listOf(Links.self(Uris.Person.makeSpecific(person.id)))
    )
}