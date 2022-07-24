package pt.isel.ps.project.unittests.building

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION

class BuildingUrisTests {

    @Test
    fun `Make valid building specific path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "$VERSION/companies/321/buildings/123"

        val path = Uris.Companies.Buildings.makeSpecific(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific with pagination path`() {
        val companyId = 321L
        val buildingId = 123L
        val pageValue = 1

        val expectedPath = "$VERSION/companies/321/buildings/123?page=1"

        val path = Uris.Companies.Buildings.makeSpecificWithPage(companyId, buildingId, pageValue)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid specific with pagination template path`() {
        val companyId = 321L
        val buildingId = 123L

        val expectedPath = "$VERSION/companies/321/buildings/123{?page}"

        val path = Uris.Companies.Buildings.makeSpecificPaginationTemplate(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid building activate path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "$VERSION/companies/321/buildings/123/activate"

        val path = Uris.Companies.Buildings.makeActivate(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid building deactivate path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "$VERSION/companies/321/buildings/123/deactivate"

        val path = Uris.Companies.Buildings.makeDeactivate(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid building manager path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "$VERSION/companies/321/buildings/123/manager"

        val path = Uris.Companies.Buildings.makeManager(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}