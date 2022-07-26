package pt.isel.ps.project.integrationtests.ticket

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
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.ADD_TICKET_RATE
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.CHANGE_TICKET_STATE
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.CREATED_TICKET
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.EMPLOYEE_STATES
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.GET_SPECIFIC_EMPLOYEES
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.GET_TICKET
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.GET_TICKETS
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.GROUP_TICKET
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.REMOVE_EMPLOYEE
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.SET_EMPLOYEE
import pt.isel.ps.project.integrationtests.ticket.TicketExpectedRepresentations.UPDATED_TICKET
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.util.serializeToJson
import utils.Utils
import utils.ignoreCreationTimestamp
import utils.ignoreTimestamp
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketTests {
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

    private final val userHeaders = HttpHeaders().apply {
        add("Request-Origin", "Mobile")
        setBearerAuth(Utils.franciscoUserToken)
    }

    @Test
    fun `Get tickets`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Tickets.BASE_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(GET_TICKETS)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Get ticket`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeSpecific(ticketId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()?.ignoreCreationTimestamp()).isEqualTo(GET_TICKET)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Create ticket`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Tickets.BASE_PATH}"

        val person = CreateTicketEntity(
            "Ticket subject test",
            "Ticket description",
            "5abd4089b7921fd6af09d1cc1cbe5220",
            "Person name",
            "person@gmail.com",
            null
        )

        val req = HttpEntity<String>(person.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.POST, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(CREATED_TICKET)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `Update ticket`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 2L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeSpecific(ticketId)}"

        val ticketUpdate = UpdateTicketEntity( "Ticket subject update","Ticket description update")

        val req = HttpEntity<String>(ticketUpdate.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(UPDATED_TICKET)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Change ticket state`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeState(ticketId)}"

        val newStateId = 6
        val ticketState = ChangeTicketStateEntity( newStateId)

        val req = HttpEntity<String>(ticketState.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(CHANGE_TICKET_STATE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Add ticket rate`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 3L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeRate(ticketId)}"

        val rate = 5
        val ticketState = TicketRateEntity(rate)

        val req = HttpEntity<String>(ticketState.serializeToJson(), userHeaders.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body?.ignoreTimestamp()).isEqualTo(ADD_TICKET_RATE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Get specific employees`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 2L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeEmployee(ticketId)}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body).isEqualTo(GET_SPECIFIC_EMPLOYEES)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Set employee`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 2L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeEmployee(ticketId)}"

        val ticketEmployee = TicketEmployeeEntity(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"))
        val req = HttpEntity<String>(ticketEmployee.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(SET_EMPLOYEE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Remove employee`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 1L
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeEmployee(ticketId)}"

         val res = client.exchange(url, HttpMethod.DELETE, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body).isEqualTo(REMOVE_EMPLOYEE)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Group ticket`() {
        Assertions.assertThat(client).isNotNull

        val ticketId = 2L
        val parentId = 1L
        val parent = ParentTicketEntity(parentId)

        val req = HttpEntity<String>(parent.serializeToJson(), headers.apply { contentType = MediaType.APPLICATION_JSON })
        val url = "${Utils.DOMAIN}$port${Uris.Tickets.makeGroup(ticketId)}"

        val res = client.exchange(url, HttpMethod.PUT, req, String::class.java)

        Assertions.assertThat(res.body).isEqualTo(GROUP_TICKET)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Employee states`() {
        Assertions.assertThat(client).isNotNull

        val url = "${Utils.DOMAIN}$port${Uris.Tickets.EMPLOYEE_STATES_PATH}"

        val res = client.exchange(url, HttpMethod.GET, HttpEntity<String>(headers), String::class.java)

        Assertions.assertThat(res.body).isEqualTo(EMPLOYEE_STATES)
        Assertions.assertThat(res.headers.contentType).isEqualTo(QRreportJsonModel.MEDIA_TYPE)
        Assertions.assertThat(res.statusCode).isEqualTo(HttpStatus.OK)
    }
}