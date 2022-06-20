package pt.isel.ps.project.unittests.building

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris

class BuildingUrisTests {

    @Test
    fun `Make valid building specific path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "${Uris.Companies.BASE_PATH}/321/buildings/123"

        val path = Uris.Companies.Buildings.makeSpecific(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid building activate path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "${Uris.Companies.BASE_PATH}/321/buildings/123/activate"

        val path = Uris.Companies.Buildings.makeActivate(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid building manager path`() {
        val companyId = 321L
        val buildingId = 123L
        val expectedPath = "${Uris.Companies.BASE_PATH}/321/buildings/123/manager"

        val path = Uris.Companies.Buildings.makeManager(companyId, buildingId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}