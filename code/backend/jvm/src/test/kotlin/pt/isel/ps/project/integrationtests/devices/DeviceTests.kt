package pt.isel.ps.project.integrationtests.devices

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
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.CHANGE_CATEGORY_DEVICE
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.CREATED_DEVICE
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.DEACTIVATE_DEVICE
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.GET_DEVICE
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.GET_DEVICES
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.ROOM_DEVICE
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.ROOM_DEVICES
import pt.isel.ps.project.integrationtests.devices.DeviceExpectedRepresentations.UPDATED_DEVICE
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.device.ChangeDeviceCategoryEntity
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.UpdateDeviceEntity
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.ignoreTimestamp

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceTests {
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
    fun `Get devices`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Devices.BASE_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_DEVICES)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create device`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Devices.BASE_PATH}"
        val categoryId = 1
        val device = CreateDeviceEntity("New device name", categoryId)
        val req = HttpEntity<String>(device.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(CREATED_DEVICE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `Get device`() {
        Assertions.assertThat(client).isNotNull

        val deviceId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Devices.makeSpecific(deviceId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_DEVICE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Update device`() {
        Assertions.assertThat(client).isNotNull

        val deviceId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Devices.makeSpecific(deviceId)}"

        val deviceUpdate = UpdateDeviceEntity("New device name updated")
        val req = HttpEntity<String>(deviceUpdate.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(UPDATED_DEVICE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Deactivate device`() {
        Assertions.assertThat(client).isNotNull

        val deviceId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Devices.makeDeactivate(deviceId)}"

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(DEACTIVATE_DEVICE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Change device category`() {
        Assertions.assertThat(client).isNotNull

        val deviceId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Devices.makeCategory(deviceId)}"

        val categoryId = 2
        val device = ChangeDeviceCategoryEntity(categoryId)
        val req = HttpEntity<String>(device.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(CHANGE_CATEGORY_DEVICE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Get room devices`() {
        Assertions.assertThat(client).isNotNull

        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Companies.Buildings.Rooms.makeDevices(companyId, buildingId, roomId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(ROOM_DEVICES)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Get room device`() {
        Assertions.assertThat(client).isNotNull

        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val deviceId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Companies.Buildings.Rooms.makeSpecificDevice(companyId, buildingId, roomId, deviceId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(ROOM_DEVICE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}