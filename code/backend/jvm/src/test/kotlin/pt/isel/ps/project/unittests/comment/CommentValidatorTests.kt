package pt.isel.ps.project.unittests.comment

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.comment.CommentEntity
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.util.Validator

class CommentValidatorTests {

    @Test
    fun `Create with valid comment`() {

        val comment = CreateCommentEntity("Comment test")

        Assertions.assertThat(Validator.Ticket.Comment.verifyCommentInput(comment)).isTrue
    }


    @Test
    fun `Throws exception when comment is created with an invalid comment length`() {
        val invComment = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

        val comment = CreateCommentEntity(invComment)
        val expectedEx =
            InvalidParameterException(
                Errors.BadRequest.Message.INVALID_REQ_PARAMS,
                listOf(
                    InvalidParameter(
                        CommentEntity.COMMENT,
                        Errors.BadRequest.Locations.BODY,
                        Errors.BadRequest.Message.Ticket.Comment.INVALID_COMMENT_LENGTH
                    )
                )
            )

        Assertions.assertThatThrownBy { Validator.Ticket.Comment.verifyCommentInput(comment) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when comment is created with an blank comment`() {
        val comment = CreateCommentEntity("    ")

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    CommentEntity.COMMENT,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Ticket.Comment.verifyCommentInput(comment) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Update with valid comment`() {

        val comment = CreateCommentEntity("New comment test")

        Assertions.assertThat(Validator.Ticket.Comment.verifyCommentInput(comment)).isTrue
    }

    @Test
    fun `Throws exception when comment is updated with an blank comment`() {
        val comment = CreateCommentEntity("    ")

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    CommentEntity.COMMENT,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Ticket.Comment.verifyCommentInput(comment) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when comment is updated with an invalid comment length`() {
        val invComment = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                         "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

        val comment = CreateCommentEntity(invComment)
        val expectedEx =
            InvalidParameterException(
                Errors.BadRequest.Message.INVALID_REQ_PARAMS,
                listOf(
                    InvalidParameter(
                        CommentEntity.COMMENT,
                        Errors.BadRequest.Locations.BODY,
                        Errors.BadRequest.Message.Ticket.Comment.INVALID_COMMENT_LENGTH
                    )
                )
            )

        Assertions.assertThatThrownBy { Validator.Ticket.Comment.verifyCommentInput(comment) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}