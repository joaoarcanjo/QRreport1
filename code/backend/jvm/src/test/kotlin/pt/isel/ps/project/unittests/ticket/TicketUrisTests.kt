package pt.isel.ps.project.unittests.ticket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION

class TicketUrisTests {

    @Test
    fun `Make valid ticket specific path`() {
        val ticketId = 112L
        val expectedPath = "$VERSION/tickets/112"

        val path = Uris.Tickets.makeSpecific(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket employee path`() {
        val ticketId = 112L
        val expectedPath = "$VERSION/tickets/112/employee"

        val path = Uris.Tickets.makeEmployee(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket possible employees pagination path`() {
        val ticketId = 112L
        val expectedPath = "$VERSION/tickets/112/employee{?page}"

        val path = Uris.Tickets.makePossibleEmployeesPagination(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket rate path`() {
        val ticketId = 112L
        val expectedPath = "$VERSION/tickets/112/rate"

        val path = Uris.Tickets.makeRate(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket state path`() {
        val ticketId = 112L
        val expectedPath = "$VERSION/tickets/112/state"

        val path = Uris.Tickets.makeState(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid ticket group path`() {
        val ticketId = 112L
        val expectedPath = "$VERSION/tickets/112/group"

        val path = Uris.Tickets.makeGroup(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}