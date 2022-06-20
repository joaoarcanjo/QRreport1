package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.comment.CommentDto
import pt.isel.ps.project.model.comment.CommentItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.CommentResponses.COMMENT_MAX_PAGE_SIZE
import pt.isel.ps.project.responses.CommentResponses.createCommentRepresentation
import pt.isel.ps.project.responses.CommentResponses.deleteCommentRepresentation
import pt.isel.ps.project.responses.CommentResponses.getCommentRepresentation
import pt.isel.ps.project.responses.CommentResponses.getCommentsRepresentation
import pt.isel.ps.project.responses.CommentResponses.updateCommentRepresentation
import pt.isel.ps.project.service.CommentService

@RestController
class CommentController(private val service: CommentService) {

    @GetMapping(Uris.Tickets.Comments.BASE_PATH)
    fun getComments(@PathVariable ticketId: Long): QRreportJsonModel {
        val comments = service.getComments(ticketId)
        return getCommentsRepresentation(
            comments,
            ticketId,
            CollectionModel(1, COMMENT_MAX_PAGE_SIZE, comments.collectionSize),
            null)
    }

    @PostMapping(Uris.Tickets.Comments.BASE_PATH)
    fun createComment(
        @PathVariable ticketId: Long,
        @RequestBody comment: CreateCommentEntity
    ): ResponseEntity<QRreportJsonModel> {
        return createCommentRepresentation(ticketId, service.createComment(ticketId, comment))
    }

    @GetMapping(Uris.Tickets.Comments.SPECIFIC_PATH)
    fun getComment(@PathVariable ticketId: Long, @PathVariable commentId: Long): QRreportJsonModel {
        return getCommentRepresentation(ticketId, service.getComment(ticketId, commentId))
    }

    @PutMapping(Uris.Tickets.Comments.SPECIFIC_PATH)
    fun updateComment(
        @PathVariable ticketId: Long,
        @PathVariable commentId: Long,
        @RequestBody comment: CreateCommentEntity
    ): ResponseEntity<QRreportJsonModel> {
        return updateCommentRepresentation(ticketId, service.updateComment(ticketId, commentId, comment))
    }

    @DeleteMapping(Uris.Tickets.Comments.SPECIFIC_PATH)
    fun deactivateCompany(@PathVariable ticketId: Long, @PathVariable commentId: Long): ResponseEntity<QRreportJsonModel> {
        return deleteCommentRepresentation(ticketId, service.deleteComment(ticketId, commentId))
    }
}