package pt.isel.ps.project.service

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.PersonDao
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class PersonService(jdbi: Jdbi) {

    private val personDao = jdbi.onDemand<PersonDao>()

    fun getPersons(): PersonsDto {
        return personDao.getPersons().deserializeJsonTo()
    }
}