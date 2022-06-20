package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.CommentDao
import pt.isel.ps.project.exception.Errors.InternalServerError.Message.INTERNAL_ERROR
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.comment.*
import pt.isel.ps.project.util.Validator.Ticket.Comment.verifyCommentInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class CommentService(val commentDao: CommentDao) {

    //@Transactional(isolation = Isolation.READ_COMMITTED)
    fun getComments(ticketId: Long): CommentsDto {
        return commentDao.getComments(ticketId).deserializeJsonTo()
    }

    //@Transactional(isolation = Isolation.READ_COMMITTED)
    fun getComment(ticketId: Long, commentId: Long): CommentDto {
        return commentDao.getComment(ticketId, commentId).deserializeJsonTo()
    }

    //@Transactional(isolation = Isolation.SERIALIZABLE)
    fun createComment(ticketId: Long, comment: CreateCommentEntity): CommentItemDto {
        verifyCommentInput(comment)
        return commentDao.createComment(ticketId, comment).getString(COMMENT_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun updateComment(ticketId: Long, commentId: Long, comment: CreateCommentEntity): CommentItemDto {
        verifyCommentInput(comment)
        return commentDao.updateComment(commentId, ticketId, comment).getString(COMMENT_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }

    //@Transactional(isolation = Isolation.REPEATABLE_READ)
    fun deleteComment(ticketId: Long, commentId: Long): CommentItemDto {
        return commentDao.deleteComment(commentId, ticketId).getString(COMMENT_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(INTERNAL_ERROR)
    }
}