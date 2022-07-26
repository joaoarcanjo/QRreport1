package pt.isel.ps.project.integrationtests.building

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.ACTIVATE_BUILDING
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.CHANGE_BUILDING_MANAGER
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.CREATE_BUILDING
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.DEACTIVATE_BUILDING
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.GET_BUILDING
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.GET_BUILDINGS
import pt.isel.ps.project.integrationtests.building.BuildingExpectedRepresentations.UPDATE_BUILDING
import pt.isel.ps.project.integrationtests.company.CompanyExpectedRepresentations.ACTIVATE_COMPANY
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.building.ChangeManagerEntity
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.Utils.DOMAIN
import utils.ignoreTimestamp
import java.net.URI
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildingTests {
    @Autowired
    private lateinit var client: TestRestTemplate

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jdbi: Jdbi

    private val delScript = Utils.LoadScript.getResourceFile("sql/delete_tables.sql")
    private val fillScript = Utils.LoadScript.getResourceFile("sql/insert_tables_tests.sql")

    @BeforeEach
    fun setUp() {
        jdbi.open().use { h -> h.createScript(delScript).execute(); h.createScript(fillScript).execute() }
    }

    @AfterAll
    fun cleanUp() {
        jdbi.open().use { h -> h.createScript(delScript).execute();}
    }

    private final val headers = HttpHeaders().apply {
        add("Request-Origin", "Mobile")
        setBearerAuth(Utils.diogoAdminToken)
    }

    @Test
    fun `Get buildings`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeBase(companyId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_BUILDINGS)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create building`() {
        assertThat(client).isNotNull
        val newBuildingId = 4L
        val companyId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeBase(companyId)}"

        val building = CreateBuildingEntity("C", 4, UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"))
        val req = HttpEntity<String>(building.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(CREATE_BUILDING)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(res.headers.location).isEqualTo(URI.create(Uris.Companies.Buildings.makeSpecific(companyId, newBuildingId)))
    }

    @Test
    fun `Get building`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeSpecific(companyId, buildingId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_BUILDING)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Update building`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeSpecific(companyId, buildingId)}"

        val building = UpdateBuildingEntity("C.v2", 5)
        val req = HttpEntity<String>(building.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(UPDATE_BUILDING)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Deactivate building`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeDeactivate(companyId, buildingId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(DEACTIVATE_BUILDING)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Activate building`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 3L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeActivate(companyId, buildingId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(ACTIVATE_BUILDING)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Change building manager`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 2L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeManager(companyId, buildingId)}"
        val manager = ChangeManagerEntity(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"))
        val req = HttpEntity<String>(manager.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(CHANGE_BUILDING_MANAGER)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}