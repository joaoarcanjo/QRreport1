package pt.isel.ps.project.unittests.ticket

import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.comment.CommentDto
import pt.isel.ps.project.model.comment.CommentItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.company.CompanyItemDto
import pt.isel.ps.project.model.device.DeviceItemDto
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.representations.DEFAULT_DIRECTION
import pt.isel.ps.project.model.representations.DEFAULT_SORT
import pt.isel.ps.project.model.room.RoomItemDto
import pt.isel.ps.project.model.state.EmployeeStateDto
import pt.isel.ps.project.model.ticket.*
import pt.isel.ps.project.service.TicketService
import utils.Utils
import utils.ignoreTimestamp
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketServiceTests {

    @Autowired
    private lateinit var service: TicketService

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
    fun `Get tickets default`() {
        val expectedTickets = TicketsDto(
            listOf(
                TicketItemDto(
                    1,
                    "Fuga de água",
                    "A sanita está a deixar sair água por baixo",
                    "ISEL",
                    "A",
                    "1 - Bathroom",
                    "Fixing",
                    "Fixing"
                ),
                TicketItemDto(
                    2,
                    "Infiltração na parede",
                    "Os cães começaram a roer a corda e acabaram por fugir todos, foi assustador",
                    "ISEL",
                    "A",
                    "1 - Bathroom",
                    "Waiting analysis",
                    "To assign"
                ),
                TicketItemDto(
                    3,
                    "Archived ticket",
                    "Archived ticket description",
                    "ISEL",
                    "A",
                    "1 - Bathroom",
                    "Fixing",
                    "Fixing"
                )
            ),
            3)

        val tickets = service.getTickets(adminUser, 1,1, DEFAULT_DIRECTION, DEFAULT_SORT, 1, null)

        tickets.tickets?.forEach { ticket -> Assertions.assertThat(expectedTickets.tickets?.contains(ticket)).isEqualTo(true)}

        Assertions.assertThat(expectedTickets.ticketsCollectionSize).isEqualTo(tickets.ticketsCollectionSize)
    }

    @Test
    fun `Get ticket`() {

        val ticketDtoExpected = TicketDto(
            1,
            "Fuga de água",
            "A sanita está a deixar sair água por baixo",
            null,
            "Fixing",
            "Fixing",
            null,
            listOf(
                EmployeeStateDto(6, "Completed")
            )
        )

        val ticketCommentsDto = CommentsDto(
            listOf(
                CommentDto(
                    CommentItemDto(1, "Esta sanita não tem arranjo, vou precisar de uma nova.", null),
                    PersonItemDto(
                        UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"),
                        "Zé Manuel",
                        "965555555",
                        "zeze@fixings.com",
                        listOf("employee"),
                        listOf("water", "electricity"),
                        ("active"))
                ),
                CommentDto(
                    CommentItemDto(2, "Tente fazer o possível para estancar a fuga.", null),
                    PersonItemDto(
                        UUID.fromString("4b341de0-65c0-4526-8898-24de463fc315"),
                        "Diogo Novo",
                        "961111111",
                        "diogo@qrreport.com",
                        listOf("admin", "manager"),
                        null,
                        ("active"))
                )
            ),
            2,
            "Fixing",
            false
        )

        val ticketPerson = PersonItemDto(
            UUID.fromString("b555b6fc-b904-4bd9-8c2b-4895738a437c"),
            "Francisco Ludovico",
            "9653456345",
            "ludviks@gmail.com",
            listOf("user"),
            null,
            "active"
        )

        val ticketCompany = CompanyItemDto(1, "ISEL", "active", null)
        val ticketBuilding = BuildingItemDto(1, "A", 4, "active", null)
        val ticketRoom = RoomItemDto(1, "1 - Bathroom", 1, "active", null)
        val ticketDevice = DeviceItemDto(1, "Toilet1", "water", "active", null)
        val ticketEmployee = PersonItemDto(
            UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"),
            "Zé Manuel",
            "965555555",
            "zeze@fixings.com",
            listOf("employee"),
            listOf("water", "electricity"),
            "active"
        )
        val ticketParent = null

        val ticket = service.getTicket(1, adminUser)

        Assertions.assertThat(ticket.ticket.ignoreTimestamp()).isEqualTo(ticketDtoExpected)

        ticketCommentsDto.comments?.map { comment -> ticket.ticketComments.comments?.contains(comment) }
        Assertions.assertThat(ticket.ticketComments.collectionSize).isEqualTo(ticketCommentsDto.collectionSize)
        Assertions.assertThat(ticket.ticketComments.isTicketChild).isEqualTo(ticketCommentsDto.isTicketChild)
        Assertions.assertThat(ticket.ticketComments.ticketState).isEqualTo(ticketCommentsDto.ticketState)

        Assertions.assertThat(ticket.person).isEqualTo(ticketPerson)
        Assertions.assertThat(ticket.company.ignoreTimestamp()).isEqualTo(ticketCompany)
        Assertions.assertThat(ticket.building.ignoreTimestamp()).isEqualTo(ticketBuilding)
        Assertions.assertThat(ticket.room.ignoreTimestamp()).isEqualTo(ticketRoom)
        Assertions.assertThat(ticket.device.ignoreTimestamp()).isEqualTo(ticketDevice)
        Assertions.assertThat(ticket.employee).isEqualTo(ticketEmployee)
        Assertions.assertThat(ticket.parentTicket).isEqualTo(ticketParent)
    }

    @Test
    fun `Create ticket`() {

        val createTicket = CreateTicketEntity(
            "Subject test",
            "Description test",
            "5abd4089b7921fd6af09d1cc1cbe5220",
            "António Genebra",
            "antonio@gmail.com",
            null
        )

        val expectedTicket = TicketItemDto(
            4,
            "Subject test",
            "Description test",
            "ISEL",
            "A",
            "1 - Bathroom",
            "Waiting analysis",
            "To assign"
        )

        val ticket = service.createTicket(createTicket)

        Assertions.assertThat(ticket).isEqualTo(expectedTicket)
    }

    @Test
    fun `Update ticket`() {
        val expectedTicket = TicketItemDto(
            2,
            "Subject test",
            "Description test",
            "ISEL",
            "A",
            "1 - Bathroom",
            "Waiting analysis",
            "To assign"
        )
        val updateTicket = UpdateTicketEntity("Subject test", "Description test")

        val ticket = service.updateTicket(2, updateTicket, adminUser)

        Assertions.assertThat(ticket).isEqualTo(expectedTicket)
    }

    @Test
    fun `Change ticket state`() {
        val ticketNewState = ChangeTicketStateEntity(6)

        val expectedTicket = TicketItemDto(
            1,
            "Fuga de água",
            "A sanita está a deixar sair água por baixo",
            "ISEL",
            "A",
            "1 - Bathroom",
            "Completed",
            "Completed"
        )

        val ticket = service.changeTicketState(1, ticketNewState, adminUser)

        Assertions.assertThat(ticket).isEqualTo(expectedTicket)
    }

    @Test
    fun `Add ticket rate`() {
        val ticketRate = TicketRateEntity(3)

        val expectedTicket = TicketRate(
            3,
            "Archived ticket",
            "Archived ticket description",
            "Fixing",
            "Fixing",
            3
        )

        val ticket = service.addTicketRate(3, ticketRate, adminUser)

        Assertions.assertThat(ticket).isEqualTo(expectedTicket)
    }

    @Test
    fun `Get specific employees`() {

        val expectedTicket = PersonsDto(
            listOf(
                PersonItemDto(
                    UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"),
                    "Zé Manuel",
                    "965555555",
                    "zeze@fixings.com",
                    listOf("employee"),
                    listOf("water", "electricity"),
                    "active"
                )
            ), 1
        )

        val ticket = service.getSpecificEmployees(2, adminUser, 1)

        Assertions.assertThat(ticket).isEqualTo(expectedTicket)
    }

    @Test
    fun `Set ticket employee`() {

        val ticketEmployee = TicketEmployeeEntity(UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"))
        val expected = TicketEmployee(
            TicketItemDto(
                2,
                "Infiltração na parede",
                "Os cães começaram a roer a corda e acabaram por fugir todos, foi assustador",
                "ISEL",
                "A",
                "1 - Bathroom",
                "Not started",
                "Not started"
            ),
            PersonItemDto(
                UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"),
                "Zé Manuel",
                "965555555",
                "zeze@fixings.com",
                listOf("employee"),
                listOf("water", "electricity"),
                "active"
            )
        )

        val ticket = service.setEmployee(2, ticketEmployee, adminUser)

        Assertions.assertThat(ticket).isEqualTo(expected)
    }

    @Test
    fun `Remove ticket employee`() {

        val expected = TicketEmployee(
            TicketItemDto(
                1,
                "Fuga de água",
                "A sanita está a deixar sair água por baixo",
                "ISEL",
                "A",
                "1 - Bathroom",
                "Waiting analysis",
                "To assign"
            ),
            PersonItemDto(
                UUID.fromString("c2b393be-d720-4494-874d-43765f5116cb"),
                "Zé Manuel",
                "965555555",
                "zeze@fixings.com",
                listOf("employee"),
                listOf("water", "electricity"),
                "active"
            )
        )

        val ticket = service.removeEmployee(1, adminUser)

        Assertions.assertThat(ticket).isEqualTo(expected)
    }

    @Test
    fun `Group ticket`() {

        val expected =
            TicketItemDto(
                2,
                "Infiltração na parede",
                "Os cães começaram a roer a corda e acabaram por fugir todos, foi assustador",
                "ISEL",
                "A",
                "1 - Bathroom",
                "Fixing",
                "Fixing"
            )

        val ticket = service.groupTicket(2, 1, adminUser)

        Assertions.assertThat(ticket).isEqualTo(expected)
    }

    @Test
    fun `Get employees states`() {

        val expected =
            EmployeeStatesDto(
                listOf(
                    EmployeeState(1, "To assign"),
                    EmployeeState(2, "Refused"),
                    EmployeeState(3, "Not started"),
                    EmployeeState(4, "Fixing"),
                    EmployeeState(5, "Waiting for material")
                ), 7
            )

        val states = service.getEmployeeStates(1, adminUser)

        Assertions.assertThat(states).isEqualTo(expected)
    }
}