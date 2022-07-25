package pt.isel.ps.project.unittests.room

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.device.DeviceItemDto
import pt.isel.ps.project.model.device.DevicesDto
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.service.RoomService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoomServiceTests {
    @Autowired
    private lateinit var service: RoomService

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
    fun `Get rooms default`() {
        val companyId = 1L
        val buildingId = 1L
        val expectedRooms = RoomsDto(
            listOf(
                RoomItemDto(1, "1 - Bathroom", 1, "active", null),
                RoomItemDto(2, "2", 1, "active", null),
            ),
            2, "active")

        val roomsDto = service.getRooms(adminUser, companyId, buildingId, DEFAULT_PAGE)

        Assertions.assertThat(roomsDto.ignoreTimestamps()).isEqualTo(expectedRooms)
    }

    @Test
    fun `Create room`() {
        val companyId = 1L
        val buildingId = 1L
        val roomEntity = CreateRoomEntity("99", 1)
        val expectedRoom = RoomItemDto(5, "99", 1, "active", null)

        val roomDto = service.createRoom(adminUser, companyId, buildingId, roomEntity)

        Assertions.assertThat(roomDto.ignoreTimestamp()).isEqualTo(expectedRoom)
    }

    @Test
    fun `Get room`() {
        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val expectedRoom = RoomDto(
            RoomItemDto(1, "1 - Bathroom", 1, "active", null),
            DevicesDto(
                listOf(DeviceItemDto(1, "Toilet1", "water", "active", null)),
                1
            ),
        )

        val roomDto = service.getRoom(adminUser, companyId, buildingId, roomId)

        Assertions.assertThat(roomDto.ignoreTimestamp()).isEqualTo(expectedRoom)
    }

    @Test
    fun `Get room with non existent id`() {
        val companyId = 1L
        val buildingId = 1L
        val roomId = 99L
        val expMsg = "ERROR: resource-not-found\n" +
                "  Detail: room\n" +
                "  Hint: 99"

        Assertions.assertThatThrownBy { service.getRoom(adminUser, companyId, buildingId, roomId) }
            .hasMessageContaining(expMsg)
    }

    @Test
    fun `Update room`() {
        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val roomEntity = UpdateRoomEntity("1")
        val expectedRoom = RoomItemDto(1, "1", 1, "active", null)

        val roomItemDto = service.updateRoom(adminUser, companyId, buildingId, roomId, roomEntity)

        Assertions.assertThat(roomItemDto.ignoreTimestamp()).isEqualTo(expectedRoom)
    }

    @Test
    fun `Deactivate room`() {
        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val expectedRoom = RoomItemDto(1, "1 - Bathroom", 1, "inactive", null)

        val roomItemDto = service.deactivateRoom(adminUser, companyId, buildingId, roomId)

        Assertions.assertThat(roomItemDto.ignoreTimestamp()).isEqualTo(expectedRoom)
    }

    @Test
    fun `Activate room`() {
        val companyId = 1L
        val buildingId = 2L
        val roomId = 4L
        val expectedRoom = RoomItemDto(4, "57", 3, "active", null)

        val roomItemDto = service.activateRoom(adminUser, companyId, buildingId, roomId)

        Assertions.assertThat(roomItemDto.ignoreTimestamp()).isEqualTo(expectedRoom)
    }

    @Test
    fun `Add room device`() {
        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val deviceId = 2L
        val deviceEntity = AddDeviceEntity(deviceId)
        val expectedRoom = RoomDeviceDto(
            RoomItemDto(1, "1 - Bathroom", 1, "active", null),
            DeviceItemDto(2, "Lights", "electricity", "active", null),
        )

        val room = service.addRoomDevice(adminUser, companyId, buildingId, roomId, deviceEntity)

        Assertions.assertThat(room.ignoreTimestamp()).isEqualTo(expectedRoom)
    }

    @Test
    fun `Remove room device`() {
        val companyId = 1L
        val buildingId = 1L
        val roomId = 1L
        val deviceId = 1L
        val expectedRoom = RoomDeviceDto(
            RoomItemDto(1, "1 - Bathroom", 1, "active", null),
            DeviceItemDto(1, "Toilet1", "water", "active", null),
        )

        val room = service.removeRoomDevice(adminUser, companyId, buildingId, roomId, deviceId)

        Assertions.assertThat(room.ignoreTimestamp()).isEqualTo(expectedRoom)
    }
}