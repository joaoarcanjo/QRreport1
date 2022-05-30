package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.comment.CommentDto
import pt.isel.ps.project.model.comment.CommentItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.comment.InputCommentEntity
import pt.isel.ps.project.model.company.*
import pt.isel.ps.project.service.CommentService

@RestController
class CommentController(private val service: CommentService) {

    @GetMapping(Uris.Tickets.Comments.BASE_PATH)
    fun getComments(@PathVariable ticketId: Long): CommentsDto {
        return service.getComments(ticketId)
    }

    @PostMapping(Uris.Tickets.Comments.BASE_PATH)
    fun createComment(@PathVariable ticketId: Long, @RequestBody comment: InputCommentEntity): CommentItemDto {
        return service.createComment(ticketId, comment)
    }

    @GetMapping(Uris.Tickets.Comments.SPECIFIC_PATH)
    fun getComment(@PathVariable ticketId: Long, @PathVariable commentId: Long): CommentDto {
        return service.getComment(ticketId, commentId)
    }

    @PutMapping(Uris.Tickets.Comments.SPECIFIC_PATH)
    fun updateComment(
        @PathVariable ticketId: Long,
        @PathVariable commentId: Long,
        @RequestBody comment: InputCommentEntity
    ): CommentItemDto {
        return service.updateComment(ticketId, commentId, comment)
    }

    @DeleteMapping(Uris.Tickets.Comments.SPECIFIC_PATH)
    fun deactivateCompany(@PathVariable ticketId: Long, @PathVariable commentId: Long): CommentItemDto {
        return service.deleteComment(ticketId, commentId)
    }
}