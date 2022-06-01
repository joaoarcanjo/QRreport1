package pt.isel.ps.project.service

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.CommentDao
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.comment.*
import pt.isel.ps.project.util.Validator.Ticket.Comment.verifyCommentInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class CommentService(jdbi: Jdbi) {

    private val commentDao = jdbi.onDemand<CommentDao>()

    fun getComments(ticketId: Long): CommentsDto {
        return commentDao.getComments(ticketId).deserializeJsonTo()
    }

    fun getComment(ticketId: Long, commentId: Long): CommentDto {
        return commentDao.getComment(ticketId, commentId).deserializeJsonTo()
    }

    fun createComment(ticketId: Long, comment: InputCommentEntity): CommentItemDto {
        verifyCommentInput(comment)
        return commentDao.createComment(ticketId, comment).getString(COMMENT_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun updateComment(ticketId: Long, commentId: Long, comment: InputCommentEntity): CommentItemDto {
        verifyCommentInput(comment)
        return commentDao.updateComment(commentId, ticketId, comment).getString(COMMENT_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deleteComment(ticketId: Long, commentId: Long): CommentItemDto {
        return commentDao.deleteComment(commentId, ticketId).getString(COMMENT_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}