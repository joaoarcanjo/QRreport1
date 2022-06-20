package pt.isel.ps.project.unittests.ticket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.comment.CommentEntity
import pt.isel.ps.project.model.ticket.CreateTicketEntity
import pt.isel.ps.project.model.ticket.TicketEntity
import pt.isel.ps.project.model.ticket.TicketRateEntity
import pt.isel.ps.project.model.ticket.UpdateTicketEntity
import pt.isel.ps.project.util.Validator
import pt.isel.ps.project.util.Validator.Ticket.verifyCreateTicketInput
import pt.isel.ps.project.util.Validator.Ticket.verifyTicketRateInput

class TicketValidatorTests {

    @Test
    fun `Create ticket with valid hash, subject and description`() {
        val ticket = CreateTicketEntity(
            "Subject test",
            "Description test",
            "5abd4089b7921fd6af09d1cc1cbe5220",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        Assertions.assertThat(verifyCreateTicketInput(ticket)).isTrue
    }

    @Test
    fun `Throws exception when ticket is created with an invalid subject length`() {
        val ticket = CreateTicketEntity(
            "012345678901234567890123456789012345678901234567890123456789",
            "Description test",
            "D793E0C6D5BF864CCB0E64B1AAA6B9BC0FB02B2C64FAA5B8AABB97F9F54A5B90",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_SUBJECT,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Ticket.INVALID_SUBJECT_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is created with an blank subject`() {
        val ticket = CreateTicketEntity(
            "                   ",
            "Description test",
            "D793E0C6D5BF864CCB0E64B1AAA6B9BC0FB02B2C64FAA5B8AABB97F9F54A5B90",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_SUBJECT,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is created with an invalid description length`() {
        val ticket = CreateTicketEntity(
            "Subject test",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                     "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                     "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                     "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "D793E0C6D5BF864CCB0E64B1AAA6B9BC0FB02B2C64FAA5B8AABB97F9F54A5B90",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_DESCRIPTION,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Ticket.INVALID_DESCRIPTION_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is created with an blank description`() {
        val ticket = CreateTicketEntity(
            "Subject test",
            "          ",
            "D793E0C6D5BF864CCB0E64B1AAA6B9BC0FB02B2C64FAA5B8AABB97F9F54A5B90",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_DESCRIPTION,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is created with an invalid hash length`() {
        val ticket = CreateTicketEntity(
            "Subject test",
            "Description test",
            "HSAHREKCAH",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_HASH,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Ticket.INVALID_HASH_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is created with an blank hash`() {
        val ticket = CreateTicketEntity(
            "Subject test",
            "Description test",
            "               ",
            "Zé",
            "ze@gmail.com",
            "967597898"
        )

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_HASH,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Update ticket with valid subject`() {
        val company = UpdateTicketEntity("New subject", null)

        Assertions.assertThat(Validator.Ticket.verifyUpdateTicketInput(company)).isTrue
    }

    @Test
    fun `Update ticket with valid description`() {
        val company = UpdateTicketEntity("New subject", null)

        Assertions.assertThat(Validator.Ticket.verifyUpdateTicketInput(company)).isTrue
    }

    @Test
    fun `Throws exception when ticket is updated with null subject and null description`() {
        val ticket = UpdateTicketEntity(null, null)
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.UPDATE_NULL_PARAMS,
            detail = Errors.BadRequest.Message.UPDATE_NULL_PARAMS_DETAIL)

        Assertions.assertThatThrownBy { Validator.Ticket.verifyUpdateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is updated with blank subject`() {
        val ticket = UpdateTicketEntity("   ", "Description test")
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(InvalidParameter(
                TicketEntity.TICKET_SUBJECT,
                Errors.BadRequest.Locations.BODY,
                Errors.BadRequest.Message.BLANK_PARAMS
            ))
        )

        Assertions.assertThatThrownBy { Validator.Ticket.verifyUpdateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is updated with blank description`() {
        val ticket = UpdateTicketEntity("Subject test", "    ")
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(InvalidParameter(
                TicketEntity.TICKET_DESCRIPTION,
                Errors.BadRequest.Locations.BODY,
                Errors.BadRequest.Message.BLANK_PARAMS
            ))
        )

        Assertions.assertThatThrownBy { Validator.Ticket.verifyUpdateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is updated with an invalid subject length`() {
        val invSubject = "01234567890123456789012345678901234567890123456789S"
        val ticket = UpdateTicketEntity(invSubject, null)
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_SUBJECT,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Ticket.INVALID_SUBJECT_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Ticket.verifyUpdateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when ticket is updated with an invalid description length`() {
        val invDescription = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                             "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                             "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                             "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                             "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

        val ticket = UpdateTicketEntity(null, invDescription)
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_DESCRIPTION,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Ticket.INVALID_DESCRIPTION_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Ticket.verifyUpdateTicketInput(ticket) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Create rate for a ticket`() {
        val ticketRate = TicketRateEntity(1)

        Assertions.assertThat(verifyTicketRateInput(ticketRate)).isTrue
    }

    @Test
    fun `Create invalid rate`() {
        val ticketRate = TicketRateEntity(7)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    TicketEntity.TICKET_RATE,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Ticket.INVALID_RATE
                )
            )
        )

        Assertions.assertThatThrownBy { verifyTicketRateInput(ticketRate) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}