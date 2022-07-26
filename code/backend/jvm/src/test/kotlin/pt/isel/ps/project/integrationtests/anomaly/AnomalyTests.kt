package pt.isel.ps.project.integrationtests.anomaly

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
import pt.isel.ps.project.integrationtests.anomaly.AnomalyExpectedRepresentations.GET_ANOMALIES
import pt.isel.ps.project.integrationtests.anomaly.AnomalyExpectedRepresentations.CREATED_ANOMALY
import pt.isel.ps.project.integrationtests.anomaly.AnomalyExpectedRepresentations.DELETED_ANOMALY
import pt.isel.ps.project.integrationtests.anomaly.AnomalyExpectedRepresentations.UPDATED_ANOMALY
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.Utils.DOMAIN

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnomalyTests {
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
    fun `Get anomalies`() {
        assertThat(client).isNotNull
        val deviceId = 1L
        val url = "$DOMAIN$port${Uris.Devices.Anomalies.makeBase(deviceId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body).isEqualTo(GET_ANOMALIES)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create anomaly`() {
        assertThat(client).isNotNull
        val deviceId = 1L
        val url = "$DOMAIN$port${Uris.Devices.Anomalies.makeBase(deviceId)}"

        val anomaly = InputAnomalyEntity("Anomaly test")
        val req = HttpEntity<String>(anomaly.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        assertThat(res.body).isEqualTo(CREATED_ANOMALY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `Update anomaly`() {
        assertThat(client).isNotNull
        val deviceId = 1L
        val anomalyId = 1L
        val url = "$DOMAIN$port${Uris.Devices.Anomalies.makeSpecific(deviceId, anomalyId)}"

        val anomaly = InputAnomalyEntity("Anomaly test updated")
        val req = HttpEntity<String>(anomaly.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        assertThat(res.body).isEqualTo(UPDATED_ANOMALY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Delete anomaly`() {
        assertThat(client).isNotNull
        val deviceId = 1L
        val anomalyId = 1L
        val url = "$DOMAIN$port${Uris.Devices.Anomalies.makeSpecific(deviceId, anomalyId)}"

        val res = client.exchange(url, HttpMethod.DELETE, HttpEntity<String>(headers), String::class.java)

        assertThat(res.body).isEqualTo(DELETED_ANOMALY)
        assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}