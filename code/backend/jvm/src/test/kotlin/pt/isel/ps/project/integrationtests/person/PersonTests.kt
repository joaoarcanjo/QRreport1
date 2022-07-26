package pt.isel.ps.project.integrationtests.person

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
import pt.isel.ps.project.integrationtests.anomaly.AnomalyExpectedRepresentations
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.ADD_ROLE_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.ADD_SKILL_EMPLOYEE
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.ASSIGN_PERSON_COMPANY
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.BAN_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.CREATED_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.DELETED_USER
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.FIRED_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.GET_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.GET_PERSONS
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.GET_PROFILE
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.GET_ROLES
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.REHIRE_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.REMOVE_ROLE_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.REMOVE_SKILL_EMPLOYEE
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.SWITCH_ROLE_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.UNBAN_PERSON
import pt.isel.ps.project.integrationtests.person.PersonExpectedRepresentations.UPDATED_PERSON
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.model.person.*
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonTests {
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
    fun `Get persons`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Persons.BASE_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body).isEqualTo(GET_PERSONS)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create person`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Persons.BASE_PATH}"

        val person = CreatePersonEntity(
            "Person name test",
            "965536771",
            "person@isel.pt",
            "passwordTest",
            "admin",
            null,
            null
        )

        val req = HttpEntity<String>(person.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(CREATED_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `Get person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057")
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeSpecific(userId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(GET_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Get profile`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Persons.PROFILE_PATH}"

        val anomaly = InputAnomalyEntity("Person test updated")
        val req = HttpEntity<String>(anomaly.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.GET, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(GET_PROFILE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Update person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057")
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeSpecific(userId)}"

        val personUpdate = UpdatePersonEntity("Person test updated", null, "test@isel.pt", null)
        val req = HttpEntity<String>(personUpdate.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(UPDATED_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Delete user`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("b555b6fc-b904-4bd9-8c2b-4895738a437c")
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeSpecific(userId)}"

        val res = client.exchange(url, HttpMethod.DELETE, HttpEntity<String>(headers), String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(DELETED_USER)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Fire person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb")
        val companyId = 1
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeFire(userId)}".replace("{companyId}", "$companyId")

        val personFire = FireBanPersonEntity("Bad behaviour")
        val req = HttpEntity<String>(personFire.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(FIRED_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Rehire person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057")
        val companyId = 1
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeRehire(userId)}".replace("{companyId}", "$companyId")

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(REHIRE_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Ban person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("d1ad1c02-9e4f-476e-8840-c56ae8aa7057")
        val companyId = 1
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeBan(userId)}".replace("{companyId}", "$companyId")

        val personBan = FireBanPersonEntity("Bad behaviour")
        val req = HttpEntity<String>(personBan.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(BAN_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Unban person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("5e63ea2f-53cf-4546-af41-f0b3a20eac91")
        val companyId = 1
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeUnban(userId)}".replace("{companyId}", "$companyId")

        val res = client.exchange(url, HttpMethod.POST, HttpEntity<String>(headers), String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(UNBAN_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Add role to person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb")
        val roleName = "admin"
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeAddRole(userId)}"

        val personRole = AddRoleToPersonEntity(roleName, null, null)
        val req = HttpEntity<String>(personRole.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(ADD_ROLE_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Remove role to person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315")
        val roleName = "manager"
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeRemoveRole(userId)}"

        val personRole = RemoveRoleFromPersonEntity(roleName)
        val req = HttpEntity<String>(personRole.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(REMOVE_ROLE_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Add skill to person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb")
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeAddSkill(userId)}"

        val skillId = 4L
        val employeeSkill = AddRemoveSkillToEmployeeEntity(skillId)
        val req = HttpEntity<String>(employeeSkill.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(ADD_SKILL_EMPLOYEE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Remove skill to person`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb")
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeRemoveSkill(userId)}"

        val skillId = 2L
        val employeeSkill = AddRemoveSkillToEmployeeEntity(skillId)
        val req = HttpEntity<String>(employeeSkill.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(REMOVE_SKILL_EMPLOYEE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Assign person to company`() {
        Assertions.assertThat(client).isNotNull

        val userId = UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb")
        val url = "${Utils.DOMAIN}$port${Uris.Persons.makeAssignCompany(userId)}"

        val companyId = 2L
        val personCompany = AssignPersonToCompanyEntity(companyId)
        val req = HttpEntity<String>(personCompany.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(ASSIGN_PERSON_COMPANY)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Switch role`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Persons.SWITCH_ROLE}"

        val role = "manager"
        val personRole = SwitchRoleEntity(role)
        val req = HttpEntity<String>(personRole.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        //Assertions.assertThat(res.body).isEqualTo(SWITCH_ROLE_PERSON)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Get roles`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Persons.ROLES_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body).isEqualTo(GET_ROLES)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}