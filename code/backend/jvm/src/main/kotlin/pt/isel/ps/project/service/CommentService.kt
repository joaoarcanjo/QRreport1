package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.dao.CommentDao
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.comment.*
import pt.isel.ps.project.model.representations.elemsToSkip
import pt.isel.ps.project.responses.CommentResponses.COMMENT_PAGE_MAX_SIZE
import pt.isel.ps.project.util.Validator.Ticket.Comment.verifyCommentInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class CommentService(val commentDao: CommentDao) {

    fun getComments(ticketId: Long, page: Int): CommentsDto {
        return commentDao.getComments(ticketId, elemsToSkip(page, COMMENT_PAGE_MAX_SIZE)).deserializeJsonTo()
    }

    fun createComment(ticketId: Long, comment: CreateCommentEntity, user: AuthPerson): CommentItemDto {
        verifyCommentInput(comment)
        return commentDao.createComment(ticketId, comment, user.id).getString(COMMENT_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun updateComment(user: AuthPerson, ticketId: Long, commentId: Long, comment: CreateCommentEntity): CommentItemDto {
        verifyCommentInput(comment)
        return commentDao.updateComment(commentId, user.id, ticketId, comment).getString(COMMENT_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    fun deleteComment(user: AuthPerson, ticketId: Long, commentId: Long): CommentItemDto {
        return commentDao.deleteComment(commentId, user.id, ticketId).getString(COMMENT_REP)?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}