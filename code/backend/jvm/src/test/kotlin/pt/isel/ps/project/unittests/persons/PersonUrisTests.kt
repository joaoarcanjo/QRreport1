package pt.isel.ps.project.unittests.persons

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION
import java.util.*

class PersonUrisTests {

    @Test
    fun `Make valid specific person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d"

        val path = Uris.Persons.makeSpecific(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific fire person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/companies/{companyId}/persons/08226498-2244-4b38-a567-38a8f455922d/fire"

        val path = Uris.Persons.makeFire(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific rehire person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/companies/{companyId}/persons/08226498-2244-4b38-a567-38a8f455922d/rehire"

        val path = Uris.Persons.makeRehire(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific ban person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/ban"

        val path = Uris.Persons.makeBan(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific unban person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/unban"

        val path = Uris.Persons.makeUnban(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific add role person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/add-role"

        val path = Uris.Persons.makeAddRole(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific remove role person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/remove-role"

        val path = Uris.Persons.makeRemoveRole(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific add skill person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/add-skill"

        val path = Uris.Persons.makeAddSkill(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific remove skill person path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/remove-skill"

        val path = Uris.Persons.makeRemoveSkill(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific assign person to company path`() {
        val personId = UUID.fromString("08226498-2244-4b38-a567-38a8f455922d")
        val expectedPath = "$VERSION/persons/08226498-2244-4b38-a567-38a8f455922d/assign-company"

        val path = Uris.Persons.makeAssignCompany(personId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}