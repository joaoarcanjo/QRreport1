package pt.isel.ps.project.unittests.persons
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris.UNDEFINED
import pt.isel.ps.project.model.person.*
import pt.isel.ps.project.service.PersonService
import utils.Utils
import utils.changeToTest
import utils.ignoreTimestamp
import java.util.*
import kotlin.collections.LinkedHashMap

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonServiceTests {

    @Autowired
    private lateinit var service: PersonService

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
    fun `Get persons default`() {
        val expectedPersons = PersonsDto(
            listOf(
                PersonItemDto(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"), "Diogo Novo", "961111111", "diogo@qrreport.com", listOf("admin"), null, "active"),
                PersonItemDto(UUID.fromString("1f6c1014-b029-4a75-b78c-ba09c8ea474d"), "João Arcanjo", null, "joni@isel.com", listOf("admin", "admin"), null, "active"),
                PersonItemDto(UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057"), "Pedro Miguens", "963333333", "pedro@isel.com", listOf("manager"), null, "active"),
                PersonItemDto(UUID.fromString("b555b6fc-b904-4bd9-8c2b-4895738a437c"), "Zé Manuel", "965555555", "zeze@fixings.com", listOf("employee"), listOf("water"), "active"),
                PersonItemDto(UUID.fromString("b9063a7e-7ba4-42d3-99f4-1b00e00db55d"), "Francisco Ludovico", "9653456345", "ludviks@gmail.com", listOf("user"), null, "active"),
                PersonItemDto(UUID.fromString("b9063a7e-7ba4-42d3-99f4-1b00e00db55d"), "Daniela Gomes", null, "dani@isel.com", listOf("guest"), null, "active")
            )
        ,6)

        val persons = service.getPersons(adminUser,1, UNDEFINED, 1)
        expectedPersons.persons?.map { person -> assertThat(persons.persons?.contains(person)) }
        assertThat(expectedPersons.personsCollectionSize).isEqualTo(persons.personsCollectionSize)
    }

    @Test
    fun `Create person`() {
        val idTest = UUID.randomUUID()
        val createPerson = CreatePersonEntity("João Arcanjo", null, "joao@gmail.com", "joaopass", "admin", null, null)
        val expectedPerson = PersonDto(idTest, "João Arcanjo", null, "joao@gmail.com", listOf("admin"), null, null, null, "active", null, null)

        val person = service.createPerson(adminUser, createPerson)

        assertThat(person.changeToTest(idTest)).isEqualTo(expectedPerson)
    }

    @Test
    fun `Get person`() {
        val expectedPerson = PersonDetailsDto(PersonDto(UUID.fromString("1f6c1014-b029-4a75-b78c-ba09c8ea474d"), "João Arcanjo", null, "joni@isel.com", listOf("admin"), null, null, null, "active", null, null), null)

        val person = service.getPerson(adminUser, UUID.fromString("1f6c1014-b029-4a75-b78c-ba09c8ea474d"))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Update person`() {
        val updatePerson = UpdatePersonEntity("Name test", null, "email@test.pt", null)
        val expectedPerson = PersonItemDto(UUID.fromString("1f6c1014-b029-4a75-b78c-ba09c8ea474d"), "Name test", null, "email@test.pt", listOf("admin"), null, "active")

        val person = service.updatePerson(adminUser, UUID.fromString("1f6c1014-b029-4a75-b78c-ba09c8ea474d"), updatePerson)

        assertThat(person).isEqualTo(expectedPerson)
    }

    @Test
    fun `Delete user`() {
        val expectedPerson = PersonDto(UUID.fromString("b555b6fc-b904-4bd9-8c2b-4895738a437c"), "b555b6fc-b904-4bd9-8c2b-4895738a437c", null, "b555b6fc-b904-4bd9-8c2b-4895738a437c@deleted.com", listOf("user"), null, null, null, "inactive", "User deleted account", null)

        val person = service.deleteUser( UUID.fromString("b555b6fc-b904-4bd9-8c2b-4895738a437c"))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Fire person`() {
        val expectedPerson = PersonDto(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), "Zé Manuel", "965555555", "zeze@fixings.com", listOf("employee"),  listOf("water", "electricity"), null, null, "inactive", "Bad behavior", null)

        val person = service.firePerson(adminUser, UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), 1, FireBanPersonEntity("Bad behavior"))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Rehire person`() {
        val expectedPerson = PersonDto(UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057"), "Pedro Miguens", "963333333", "pedro@isel.com", listOf("manager"), null, listOf("ISEL", "IST"), null, "active", null, null)

        val person = service.rehirePerson(adminUser, UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057"), 2)

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Ban person`() {
        val expectedPerson = PersonDto(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), "Zé Manuel", "965555555", "zeze@fixings.com", listOf("employee"),  listOf("water", "electricity"), listOf("ISEL"), null, "banned", "Bad behavior", PersonDto(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"), "Diogo Novo", "961111111", "diogo@qrreport.com", listOf("admin", "manager"), null, null, null, "active", null, null))

        val person = service.banPerson(adminUser, UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), FireBanPersonEntity("Bad behavior"))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Unban person`() {
        val expectedPerson = PersonDto(UUID.fromString("5e63ea2f-53cf-4546-af41-f0b3a20eac91"), "António Ricardo", null, "antonio@isel.com", listOf("manager"), null, null, null, "active", null, null)

        val person = service.unbanPerson(adminUser, UUID.fromString("5e63ea2f-53cf-4546-af41-f0b3a20eac91"))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Add role to person`() {
        val expectedPerson = PersonDto(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), "Zé Manuel", "965555555", "zeze@fixings.com", listOf("employee", "manager"),  listOf("water", "electricity"), listOf("ISEL"), null, "active", null, null)

        val person = service.addRoleToPerson(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), AddRoleToPersonEntity("manager", 1, null))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Remove role to person`() {
        val expectedPerson = PersonDto(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"), "Diogo Novo", "961111111", "diogo@qrreport.com", listOf("admin"),  null, null, null, "active", null, null)

        val person = service.removeRoleFromPerson(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"), RemoveRoleFromPersonEntity("manager"))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Add skill to employee`() {
        val expectedPerson = PersonItemDto(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), "Zé Manuel", "965555555", "zeze@fixings.com", listOf("employee"),  listOf("water", "electricity", "window"), "active")

        val person = service.addSkillToEmployee(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), AddRemoveSkillToEmployeeEntity(4))

        assertThat(person).isEqualTo(expectedPerson)
    }

    @Test
    fun `Remove skill to employee`() {
        val expectedPerson = PersonItemDto(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), "Zé Manuel", "965555555", "zeze@fixings.com", listOf("employee"),  listOf("water"), "active")

        val person = service.removeSkillFromEmployee(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"), AddRemoveSkillToEmployeeEntity(2))

        assertThat(person).isEqualTo(expectedPerson)
    }

    @Test
    fun `Assign person to company`() {
        val expectedPerson = PersonDto(UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057"), "Pedro Miguens", "963333333", "pedro@isel.com", listOf("manager"),  null, listOf("ISEL", "IST"), null, "active", null, null)

        val person = service.assignPersonToCompany(UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057"), AssignPersonToCompanyEntity(2))

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }

    @Test
    fun `Switch person role`() {
        val expectedPerson = PersonDto(UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"), "Diogo Novo", "961111111", "diogo@qrreport.com", listOf("admin", "manager"),  null, listOf("ISEL"), null, "active", null, null)

        val person = service.switchRole(SwitchRoleEntity("manager"), adminUser)

        assertThat(person.ignoreTimestamp()).isEqualTo(expectedPerson)
    }
}