package pt.isel.ps.project.dao

import org.jdbi.v3.sqlobject.statement.SqlQuery

interface PersonDao {
    @SqlQuery("SELECT get_persons();")
    fun getPersons(): String
}