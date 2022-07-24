package pt.isel.ps.project.unittests.anomaly

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.anomaly.AnomaliesDto
import pt.isel.ps.project.model.anomaly.AnomalyItemDto
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDto
import pt.isel.ps.project.model.category.CategoryItemDto
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.service.AnomalyService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnomalyServiceTests {
    @Autowired
    private lateinit var service: AnomalyService

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
    fun `Get anomalies default`() {
        val deviceId = 1L
        val expectedAnomalies = AnomaliesDto(
            listOf(
                AnomalyItemDto(1, "The flush doesn't work"),
                AnomalyItemDto(2, "The water is overflowing"),
                AnomalyItemDto(3, "The toilet is clogged"),
                AnomalyItemDto(4, "The water is always running"),
            ),
            4)

        val anomaliesDto = service.getAnomalies(deviceId, DEFAULT_PAGE)

        Assertions.assertThat(anomaliesDto).isEqualTo(expectedAnomalies)
    }

    @Test
    fun `Create anomaly`() {
        val deviceId = 1L
        val anomalyEntity = InputAnomalyEntity("Water in the ground")
        val expectedAnomaly = AnomalyItemDto(5, "Water in the ground")

        val anomalyItemDto = service.createAnomaly(deviceId, anomalyEntity)

        Assertions.assertThat(anomalyItemDto).isEqualTo(expectedAnomaly)
    }

    @Test
    fun `Update anomaly`() {
        val deviceId = 1L
        val anomalyId = 1L
        val anomalyEntity = InputAnomalyEntity("The flush is broken")
        val expectedAnomaly = AnomalyItemDto(1, "The flush is broken")

        val anomalyItemDto = service.updateAnomaly(deviceId, anomalyId, anomalyEntity)

        Assertions.assertThat(anomalyItemDto).isEqualTo(expectedAnomaly)
    }

    @Test
    fun `Delete anomaly`() {
        val deviceId = 1L
        val anomalyId = 1L
        val expectedAnomaly = AnomalyItemDto(1, "The flush doesn't work")

        val anomalyItemDto = service.deleteAnomaly(deviceId, anomalyId)

        Assertions.assertThat(anomalyItemDto).isEqualTo(expectedAnomaly)
    }
}