package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Comment.createCommentAuthorization
import pt.isel.ps.project.auth.Authorizations.Comment.deleteCommentAuthorization
import pt.isel.ps.project.auth.Authorizations.Comment.getCommentsAuthorization
import pt.isel.ps.project.auth.Authorizations.Comment.updateCommentAuthorization
import pt.isel.ps.project.model.Uris.Tickets.Comments
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.CommentResponses.COMMENT_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.CommentResponses.createCommentRepresentation
import pt.isel.ps.project.responses.CommentResponses.deleteCommentRepresentation
import pt.isel.ps.project.responses.CommentResponses.getCommentsRepresentation
import pt.isel.ps.project.responses.CommentResponses.updateCommentRepresentation
import pt.isel.ps.project.service.CommentService

@RestController
class CommentController(private val service: CommentService) {

    @GetMapping(Comments.BASE_PATH)
    fun getComments(
        @RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int,
        @PathVariable ticketId: Long,
        user: AuthPerson,
    ): QRreportJsonModel {
        getCommentsAuthorization(user)
        val comments = service.getComments(ticketId, page)
        return getCommentsRepresentation(
            user,
            comments,
            ticketId,
            comments.ticketState,
            CollectionModel(page, COMMENT_PAGE_MAX_SIZE, comments.collectionSize),
            comments.isTicketChild,
            null)
    }

    @PostMapping(Comments.BASE_PATH)
    fun createComment(
        @PathVariable ticketId: Long,
        @RequestBody comment: CreateCommentEntity,
        user: AuthPerson
    ): ResponseEntity<QRreportJsonModel> {
        createCommentAuthorization(user)
        return createCommentRepresentation(ticketId, service.createComment(ticketId, comment, user))
    }

    @PutMapping(Comments.SPECIFIC_PATH)
    fun updateComment(
        @PathVariable ticketId: Long,
        @PathVariable commentId: Long,
        @RequestBody comment: CreateCommentEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updateCommentAuthorization(user)
        return updateCommentRepresentation(ticketId, service.updateComment(user, ticketId, commentId, comment))
    }

    @DeleteMapping(Comments.SPECIFIC_PATH)
    fun deleteComment(
        @PathVariable ticketId: Long,
        @PathVariable commentId: Long,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        deleteCommentAuthorization(user)
        return deleteCommentRepresentation(ticketId, service.deleteComment(user, ticketId, commentId))
    }
}