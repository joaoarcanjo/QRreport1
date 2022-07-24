package pt.isel.ps.project.unittests

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.person.Roles.USER
import java.util.*

class UrisQueryFilters {

    @Test
    fun `Make assign filter`() {
        val assignValue = true
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&assign=true"

        val path = Uris.Filters.makeAssign(assignValue, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make user filter`() {
        val userId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&userId=08226498-2244-4b38-a567-38a8f455922d"

        val path = Uris.Filters.makeUser(userId, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make state filter`() {
        val state = "active"
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&state=active"

        val path = Uris.Filters.makeState(state, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make role filter`() {
        val role = USER
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&role=user"

        val path = Uris.Filters.makeRole(role, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make company filter`() {
        val companyId = 1L
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&company=1"

        val path = Uris.Filters.makeCompany(companyId, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make building filter`() {
        val buildingId = 1L
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&building=1"

        val path = Uris.Filters.makeBuilding(buildingId, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make direction filter`() {
        val direction = "asc"
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&direction=asc"

        val path = Uris.Filters.makeDirection(direction, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make sort by filter`() {
        val sortBy = "date"
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&sortBy=date"

        val path = Uris.Filters.makeSortBy(sortBy, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make employee state filter`() {
        val employeeState = 1
        val uriTest = "/test?page=1"
        val expectedPath = "/test?page=1&employeeState=1"

        val path = Uris.Filters.makeEmployeeState(employeeState, uriTest)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}