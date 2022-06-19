package pt.isel.ps.project.unittests.company

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris.Companies

class CompanyUrisTests {

    @Test
    fun `Make valid company specific path`() {
        val companyId = 123L
        val expectedPath = "${Companies.BASE_PATH}/123"

        val path = Companies.makeSpecific(companyId)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid company activate path`() {
        val companyId = 123L
        val expectedPath = "${Companies.BASE_PATH}/123/activate"

        val path = Companies.makeActivate(companyId)

        assertThat(path).isEqualTo(expectedPath)
    }
}