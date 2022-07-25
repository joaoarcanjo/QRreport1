package pt.isel.ps.project.unittests.company

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris.DEFAULT_BOOL
import pt.isel.ps.project.model.Uris.UNDEFINED
import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.building.BuildingsDto
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.service.CompanyService
import utils.Utils
import utils.ignoreTimestamp
import utils.ignoreTimestamps
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyServiceTests {
    @Autowired
    private lateinit var service: CompanyService

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
    fun `Get companies default`() {
        val compDto = CompaniesDto(
            listOf(
                CompanyItemDto(1, "ISEL", "active", null),
                CompanyItemDto(2, "IST", "active", null),
                CompanyItemDto(3, "IPMA", "inactive", null),
            ),
            3)

        val companies = service.getCompanies(adminUser, null, UNDEFINED, DEFAULT_BOOL, DEFAULT_PAGE)

        assertThat(companies.ignoreTimestamps()).isEqualTo(compDto)
    }

    @Test
    fun `Create company`() {
        val comp = CreateCompanyEntity("Google Portugal")
        val expectedComp = CompanyItemDto(4, "Google Portugal", "active", null)

        val company = service.createCompany(comp)

        assertThat(company.ignoreTimestamp()).isEqualTo(expectedComp)
    }

    @Test
    fun `Get company`() {
        val expectedComp = CompanyDto(
            1,
            "ISEL",
            "active",
            null,
                BuildingsDto(listOf(
                    BuildingItemDto(1, "A", 4, "active", null),
                    BuildingItemDto(2, "F", 6, "active", null),
                    BuildingItemDto(3, "Z", 6, "inactive", null),
                ),
                3, "active")
            )

        val company = service.getCompany(expectedComp.id, adminUser)

        assertThat(company.ignoreTimestamp()).isEqualTo(expectedComp)
    }

    @Test
    fun `Get company with non existent id`() {
        val expMsg = "ERROR: resource-not-found\n" +
                "  Detail: company\n" +
                "  Hint: 99"

        assertThatThrownBy { service.getCompany(99, adminUser) }
            .hasMessageContaining(expMsg)
    }

    @Test
    fun `Update company`() {
        val comp = UpdateCompanyEntity("UEL")
        val expectedComp = CompanyItemDto(1, "UEL", "active", null)

        val company = service.updateCompany(expectedComp.id, comp)

        assertThat(company.ignoreTimestamp()).isEqualTo(expectedComp)
    }

    @Test
    fun `Deactivate company`() {
        val expectedComp = CompanyItemDto(1, "ISEL", "inactive", null)

        val company = service.deactivateCompany(expectedComp.id)

        assertThat(company.ignoreTimestamp()).isEqualTo(expectedComp)
    }

    @Test
    fun `Activate company`() {
        val expectedComp = CompanyItemDto(3, "IPMA", "active", null)

        val company = service.activateCompany(expectedComp.id)

        assertThat(company.ignoreTimestamp()).isEqualTo(expectedComp)
    }
}
