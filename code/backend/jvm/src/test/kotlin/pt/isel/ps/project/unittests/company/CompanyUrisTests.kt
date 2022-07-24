package pt.isel.ps.project.unittests.company

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris.Companies
import pt.isel.ps.project.model.Uris.VERSION

class CompanyUrisTests {

    @Test
    fun `Make valid company specific path`() {
        val companyId = 123L
        val expectedPath = "$VERSION/companies/123"

        val path = Companies.makeSpecific(companyId)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid company specific with page path`() {
        val companyId = 123L
        val pageValue = 1

        val expectedPath = "$VERSION/companies/123?page=1"

        val path = Companies.makeSpecificWithPage(companyId, pageValue)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid company specific with page template path`() {
        val companyId = 123L

        val expectedPath = "$VERSION/companies/123{?page}"

        val path = Companies.makeSpecificPaginationTemplate(companyId)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid company activate path`() {
        val companyId = 123L
        val expectedPath = "$VERSION/companies/123/activate"

        val path = Companies.makeActivate(companyId)

        assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid company deactivate path`() {
        val companyId = 123L
        val expectedPath = "$VERSION/companies/123/deactivate"

        val path = Companies.makeDeactivate(companyId)

        assertThat(path).isEqualTo(expectedPath)
    }
}