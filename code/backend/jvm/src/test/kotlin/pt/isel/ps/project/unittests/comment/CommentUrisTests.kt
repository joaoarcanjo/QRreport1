package pt.isel.ps.project.unittests.comment

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris

class CommentUrisTests {

    @Test
    fun `Make base comments path`() {
        val ticketId = 321L
        val expectedPath = "${Uris.Tickets.BASE_PATH}/321/comments"

        val path = Uris.Tickets.Comments.makeBase(ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid comment specific path`() {
        val commentId = 123L
        val ticketId = 321L
        val expectedPath = "${Uris.Tickets.BASE_PATH}/321/comments/123"

        val path = Uris.Tickets.Comments.makeSpecific(commentId, ticketId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}