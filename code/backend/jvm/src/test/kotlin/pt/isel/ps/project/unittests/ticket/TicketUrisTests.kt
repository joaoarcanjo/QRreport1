package pt.isel.ps.project.unittests.ticket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris

class TicketUrisTests {

    @Test
    fun `Make valid ticket specific path`() {
        val ticketId = 112
        val expectedPath = "${Uris.Tickets.BASE_PATH}/112"

        val path = Uris.Tickets.makeSpecific(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket employee path`() {
        val ticketId = 112
        val expectedPath = "${Uris.Tickets.BASE_PATH}/112/employee"

        val path = Uris.Tickets.makeEmployee(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket rate path`() {
        val ticketId = 112
        val expectedPath = "${Uris.Tickets.BASE_PATH}/112/rate"

        val path = Uris.Tickets.makeRate(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}