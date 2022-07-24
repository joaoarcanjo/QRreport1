package pt.isel.ps.project.unittests.building

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.building.*
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.room.RoomItemDto
import pt.isel.ps.project.model.room.RoomsDto
import pt.isel.ps.project.service.BuildingService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildingServiceTests {
    @Autowired
    private lateinit var service: BuildingService

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
    fun `Get buildings default`() {
        val companyId = 1L
        val buildingDto = BuildingsDto(
            listOf(
                BuildingItemDto(1, "A", 4, "active", null),
                BuildingItemDto(2, "F", 6, "active", null),
                BuildingItemDto(3, "Z", 6, "inactive", null),
            ),
            3)

        val buildings = service.getBuildings(adminUser, companyId, DEFAULT_PAGE)

        Assertions.assertThat(buildings.ignoreTimestamps()).isEqualTo(buildingDto)
    }

    @Test
    fun `Create building`() {
        val companyId = 1L
        val building = CreateBuildingEntity("M", 4, adminUser.id)
        val expectedBuild = BuildingItemDto(4, "M", 4, "active", null)

        val buildingDto = service.createBuilding(adminUser, companyId, building)

        Assertions.assertThat(buildingDto.ignoreTimestamp()).isEqualTo(expectedBuild)
    }

    @Test
    fun `Get building`() {
        val companyId = 1L
        val buildingId = 1L
        val expectedBuilding = BuildingDto(
            BuildingItemDto(1, "A", 4, "active", null),
            RoomsDto(
                listOf(
                    RoomItemDto(1, "1 - Bathroom", 1, "active", null),
                    RoomItemDto(2, "2", 1, "active", null),
                ), 2),
            PersonItemDto(
                UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"),
                "Diogo Novo",
                "961111111",
                "diogo@qrreport.com",
                listOf("admin", "manager"),
                null,
                "active"
            ),
        )

        val building = service.getBuilding(adminUser, companyId, buildingId)

        Assertions.assertThat(building.ignoreTimestamp()).isEqualTo(expectedBuilding)
    }

    @Test
    fun `Get building with non existent id`() {
        val companyId = 1L
        val buildingId = 99L
        val expMsg = "ERROR: resource-not-found\n" +
                "  Detail: building\n" +
                "  Hint: 99"

        Assertions.assertThatThrownBy { service.getBuilding(adminUser, companyId, buildingId) }
            .hasMessageContaining(expMsg)
    }

    @Test
    fun `Update building`() {
        val companyId = 1L
        val buildingId = 1L
        val buildingEntity = UpdateBuildingEntity("M.2", null)
        val expectedBuilding = BuildingItemDto(1, "M.2", 4, "active", null)

        val buildingItemDto = service.updateBuilding(adminUser, companyId, buildingId, buildingEntity)

        Assertions.assertThat(buildingItemDto.ignoreTimestamp()).isEqualTo(expectedBuilding)
    }

    @Test
    fun `Deactivate building`() {
        val companyId = 1L
        val buildingId = 1L
        val expectedBuilding = BuildingItemDto(1, "A", 4, "inactive", null)

        val building = service.deactivateBuilding(adminUser, companyId, buildingId)

        Assertions.assertThat(building.ignoreTimestamp()).isEqualTo(expectedBuilding)
    }

    @Test
    fun `Activate building`() {
        val companyId = 1L
        val buildingId = 3L
        val expectedBuilding = BuildingItemDto(3, "Z", 6, "active", null)

        val building = service.activateBuilding(adminUser, companyId, buildingId)

        Assertions.assertThat(building.ignoreTimestamp()).isEqualTo(expectedBuilding)
    }

    @Test
    fun `Change building manager`() {
        val companyId = 1L
        val buildingId = 1L
        val manager = ChangeManagerEntity(UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057"))
        val expectedBuilding = BuildingManagerDto(buildingId, "A", manager.manager)

        val building = service.changeBuildingManager(adminUser, companyId, buildingId, manager)

        Assertions.assertThat(building).isEqualTo(expectedBuilding)
    }
}