package pt.isel.ps.project.unittests.device

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.anomaly.AnomaliesDto
import pt.isel.ps.project.model.anomaly.AnomalyItemDto
import pt.isel.ps.project.model.device.*
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.service.DeviceService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeviceServiceTests {

    @Autowired
    private lateinit var service: DeviceService

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

    val adminUser = AuthPerson(
        UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"),
        "Diogo Novo",
        "961111111",
        "diogo@qrreport.com",
        "admin",
        null,
        listOf(LinkedHashMap<String, String>().apply {
            put("id", "1")
            put("name", "ISEL")
            put("state", "active")
            put("manages", listOf("1").toString())
        }),
        null,
        "active",
        null
    )

    @Test
    fun `Get devices default`() {
        val expectedDevices = DevicesDto(
            listOf(
                DeviceItemDto(3, "Faucet", "water", "inactive", null),
                DeviceItemDto(1, "Toilet1", "water", "active", null),
                DeviceItemDto(2, "Lights", "electricity", "active", null),
            ),
            3)

        val devices = service.getDevices(DEFAULT_PAGE)
        devices.devices?.map { device -> Assertions.assertThat(expectedDevices.devices?.contains(device.ignoreTimestamp())).isEqualTo(true) }
        Assertions.assertThat(expectedDevices.devicesCollectionSize).isEqualTo(devices.devicesCollectionSize)
    }

    @Test
    fun `Get device`() {
        val expectedDevice = DeviceDto(
            DeviceItemDto(1, "Toilet1", "water", "active", null),
            AnomaliesDto(listOf(
                AnomalyItemDto(1, "The flush doesn't work"),
                AnomalyItemDto(2, "The water is overflowing"),
                AnomalyItemDto(3, "The toilet is clogged"),
                AnomalyItemDto(4, "The water is always running"),
            ), 4)
        )

        val device = service.getDevice(expectedDevice.device.id)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedDevice)
    }

    @Test
    fun `Create device`() {
        val createDevice = CreateDeviceEntity("Dishwasher", 1)
        val expectedDevice = DeviceItemDto(4, "Dishwasher", "water", "active", null)

        val device = service.createDevice(createDevice)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedDevice)
    }

    @Test
    fun `Update device`() {
        val updateDevice = UpdateDeviceEntity("Máquinda de lavar loiça")
        val expectedDevice = DeviceItemDto(1, "Máquinda de lavar loiça", "water", "active", null)

        val device = service.updateDevice(expectedDevice.id, updateDevice)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedDevice)
    }

    @Test
    fun `Change device category`() {
        val category = ChangeDeviceCategoryEntity(2)
        val expectedDevice = DeviceItemDto(1, "Toilet1", "electricity", "active", null)

        val device = service.changeCategoryDevice(expectedDevice.id, category)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedDevice)
    }

    @Test
    fun `Deactivate device`() {
        val expectedDevice = DeviceItemDto(1, "Toilet1", "water", "inactive", null)

        val device = service.deactivateDevice(expectedDevice.id)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedDevice)
    }

    @Test
    fun `Activate device`() {
        val expectedDevice = DeviceItemDto(3, "Faucet", "water", "active", null)

        val device = service.activateDevice(expectedDevice.id)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedDevice)
    }

    @Test
    fun `Get room devices`() {
        val expectedDevices = DevicesDto(
            listOf(DeviceItemDto(1, "Toilet1", "water", "active", null)),
            1
        )

        val devices = service.getRoomDevices(adminUser, 1,1,1,1)

        Assertions.assertThat(devices.ignoreTimestamps()).isEqualTo(expectedDevices)
    }

    @Test
    fun `Get room device`() {
        val expectedQRCodeDevice = DeviceQrCodeDto(
            DeviceItemDto(1, "Toilet1", "water", "active", null),
            "5abd4089b7921fd6af09d1cc1cbe5220"
        )

        val device = service.getRoomDevice(adminUser, 1,1,1,1)

        Assertions.assertThat(device.ignoreTimestamp()).isEqualTo(expectedQRCodeDevice)
    }
}