package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.ps.project.model.Uris.Persons
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.PersonResponses.PERSON_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.PersonResponses.getPersonsRepresentation
import pt.isel.ps.project.service.PersonService

@RestController
class PersonController(private val service: PersonService) {

    @GetMapping(Persons.BASE_PATH)
    fun getPersons(): ResponseEntity<QRreportJsonModel> {
        val persons = service.getPersons()
        return getPersonsRepresentation(
            persons,
            CollectionModel(1, PERSON_PAGE_MAX_SIZE, persons.personsCollectionSize)
        )
    }
}