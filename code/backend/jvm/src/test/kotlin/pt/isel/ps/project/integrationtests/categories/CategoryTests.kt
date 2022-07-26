package pt.isel.ps.project.integrationtests.categories

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
import pt.isel.ps.project.integrationtests.categories.CategoriesExpectedRepresentations.ACTIVATE_CATEGORY
import pt.isel.ps.project.integrationtests.categories.CategoriesExpectedRepresentations.CREATE_CATEGORY
import pt.isel.ps.project.integrationtests.categories.CategoriesExpectedRepresentations.DEACTIVATE_CATEGORY
import pt.isel.ps.project.integrationtests.categories.CategoriesExpectedRepresentations.GET_CATEGORIES
import pt.isel.ps.project.integrationtests.categories.CategoriesExpectedRepresentations.UPDATE_CATEGORY
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.building.ChangeManagerEntity
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.Utils.DOMAIN
import utils.ignoreTimestamp
import java.net.URI
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryTests {
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
    fun `Get categories`() {
        assertThat(client).isNotNull
        val url = "${DOMAIN}$port${Uris.Categories.BASE_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_CATEGORIES)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create category`() {
        assertThat(client).isNotNull
        val url = "${DOMAIN}$port${Uris.Categories.BASE_PATH}"

        val category = InputCategoryEntity("farm-machines")
        val req = HttpEntity<String>(category.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(CREATE_CATEGORY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(res.headers.location).isEqualTo(URI.create(Uris.Categories.BASE_PATH))
    }

    @Test
    fun `Update category`() {
        assertThat(client).isNotNull
        val categoryId = 1L
        val url = "${DOMAIN}$port${Uris.Categories.makeSpecific(categoryId)}"

        val category = InputCategoryEntity("big-machines")
        val req = HttpEntity<String>(category.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(UPDATE_CATEGORY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Deactivate category`() {
        assertThat(client).isNotNull
        val categoryId = 4L
        val url = "${DOMAIN}$port${Uris.Categories.makeDeactivate(categoryId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(DEACTIVATE_CATEGORY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Activate building`() {
        assertThat(client).isNotNull
        val categoryId = 3L
        val url = "${DOMAIN}$port${Uris.Categories.makeActivate(categoryId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body?.ignoreTimestamp()).isEqualTo(ACTIVATE_CATEGORY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}