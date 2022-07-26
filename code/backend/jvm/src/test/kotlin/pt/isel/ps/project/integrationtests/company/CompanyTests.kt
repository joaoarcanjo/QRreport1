package pt.isel.ps.project.integrationtests.company

import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import pt.isel.ps.project.auth.LoginDto
import pt.isel.ps.project.integrationtests.company.CompanyExpectedRepresentations.ACTIVATE_COMPANY
import pt.isel.ps.project.integrationtests.company.CompanyExpectedRepresentations.CREATE_COMPANY
import pt.isel.ps.project.integrationtests.company.CompanyExpectedRepresentations.GET_COMPANIES
import pt.isel.ps.project.integrationtests.company.CompanyExpectedRepresentations.GET_COMPANY
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.Utils.DOMAIN
import utils.Utils.diogoAdminToken
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyTests {
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

//    @BeforeAll
//    fun getToken() {
//        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
//        val loginUrl = "$DOMAIN$port${Uris.Auth.LOGIN_PATH}"
//        val credentials = LoginDto("diogo@qrreport.com", "diogopass", false)
//        val reqCredentials = HttpEntity<String>(credentials.serializeToJson(), headers)
//        adminToken = client.postForEntity(loginUrl, reqCredentials, String::class.java).headers.getFirst("Authorization")!!
//    }

    private final val headers = HttpHeaders().apply {
        add("Request-Origin", "Mobile")
        setBearerAuth(diogoAdminToken)
    }

    @Test
    fun `Get companies`() {
        assertThat(client).isNotNull

        val url = "$DOMAIN$port${Uris.Companies.BASE_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(GET_COMPANIES)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create company`() {
        assertThat(client).isNotNull
        val newCompanyId = 4L
        val url = "$DOMAIN$port${Uris.Companies.BASE_PATH}"

        val company = CreateCompanyEntity("Google Portugal")
        val req = HttpEntity<String>(company.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

//        assertThat(res.body).isEqualTo(CREATE_COMPANY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(res.headers.location).isEqualTo(URI.create(Uris.Companies.makeSpecific(newCompanyId)))
    }

    @Test
    fun `Get company`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val url = "$DOMAIN$port${Uris.Companies.makeSpecific(companyId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(GET_COMPANY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Update company`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val url = "$DOMAIN$port${Uris.Companies.makeSpecific(companyId)}"

        val company = UpdateCompanyEntity("ISEL University")
        val req = HttpEntity<String>(company.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

//        assertThat(res.body).isEqualTo(UPDATE_COMPANY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Deactivate company`() {
        assertThat(client).isNotNull
        val companyId = 1L
        val url = "$DOMAIN$port${Uris.Companies.makeDeactivate(companyId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(DEACTIVATE_COMPANY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Activate company`() {
        assertThat(client).isNotNull
        val companyId = 3L
        val url = "$DOMAIN$port${Uris.Companies.makeActivate(companyId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

//        assertThat(res.body).isEqualTo(ACTIVATE_COMPANY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}