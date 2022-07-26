package pt.isel.ps.project.integrationtests.building

import org.assertj.core.api.Assertions
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
        Assertions.assertThat(client).isNotNull
        val companyId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeBase(companyId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(GET_BUILDINGS)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create building`() {
        Assertions.assertThat(client).isNotNull
        val newBuildingId = 4L
        val companyId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeBase(companyId)}"

        val building = CreateBuildingEntity("C", 4, UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"))
        val req = HttpEntity<String>(building.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

//        assertThat(res.body).isEqualTo(CREATE_BUILDING)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
        Assertions.assertThat(res.headers.location).isEqualTo(URI.create(Uris.Companies.Buildings.makeSpecific(companyId, newBuildingId)))
    }

    @Test
    fun `Get building`() {
        Assertions.assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeSpecific(companyId, buildingId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(GET_BUILDING)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Update building`() {
        Assertions.assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeSpecific(companyId, buildingId)}"

        val building = UpdateBuildingEntity("C.v2", 5)
        val req = HttpEntity<String>(building.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

//        assertThat(res.body).isEqualTo(UPDATE_BUILDING)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Deactivate building`() {
        Assertions.assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeDeactivate(companyId, buildingId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(DEACTIVATE_BUILDING)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Activate building`() {
        Assertions.assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 1L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeActivate(companyId, buildingId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(ACTIVATE_COMPANY)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Change building manager`() {
        Assertions.assertThat(client).isNotNull
        val companyId = 1L
        val buildingId = 2L
        val url = "${DOMAIN}$port${Uris.Companies.Buildings.makeManager(companyId, buildingId)}"
        val manager = ChangeManagerEntity(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"))
        val req = HttpEntity<String>(manager.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

//        assertThat(res.body).isEqualTo(CHANGE_BUILDING_MANAGER)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}