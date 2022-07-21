package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Tickets.Comments
import pt.isel.ps.project.model.Uris.Tickets.Comments.COMMENTS_PAGINATION
import pt.isel.ps.project.model.comment.CommentDto
import pt.isel.ps.project.model.comment.CommentItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.PersonResponses.getPersonItem
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin

object CommentResponses {
    const val COMMENT_PAGE_MAX_SIZE = 10

    object Actions {
        fun createComment(ticketId: Long) = QRreportJsonModel.Action(
            name = "create-comment",
            title = "Create comment",
            method = HttpMethod.POST,
            href = Comments.makeBase(ticketId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("comment", "string")
            )
        )

        fun updateComment(ticketId: Long, commentId: Long) = QRreportJsonModel.Action(
            name = "update-comment",
            title = "Update comment",
            method = HttpMethod.PUT,
            href = Comments.makeSpecific(ticketId, commentId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("comment", "string")
            )
        )

        fun deleteComment(ticketId: Long, commentId: Long) = QRreportJsonModel.Action(
            name = "delete-comment",
            title = "Delete comment",
            method = HttpMethod.DELETE,
            href = Comments.makeSpecific(ticketId, commentId)
        )
    }

    private fun getCommentItem(
        user: AuthPerson,
        ticketId: Long,
        ticketState: String,
        commentDto: CommentDto,
        isChild: Boolean,
        rel: List<String>?): QRreportJsonModel {
        val comment = commentDto.comment
        return QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            rel = rel,
            properties = comment,
            entities = listOf(getPersonItem(commentDto.person, listOf(Relations.COMMENT_AUTHOR))),
            actions = mutableListOf<QRreportJsonModel.Action>().apply {
                if (ticketState.compareTo("Archived") == 0 || isChild) return@apply
                if (user.id == commentDto.person.id) {
                    add(Actions.updateComment(ticketId, comment.id))
                    add(Actions.deleteComment(ticketId, comment.id))
                }
            },
            links = listOf(Links.self(Comments.makeSpecific(ticketId, comment.id)))
        )
    }

    fun getCommentsRepresentation(
        user: AuthPerson,
        commentsDto: CommentsDto,
        ticketId: Long,
        ticketState: String,
        collection: CollectionModel,
        isChild: Boolean,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.COMMENT, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (commentsDto.comments != null) addAll(commentsDto.comments.map {
                getCommentItem(user, ticketId, ticketState, it, isChild, listOf(Relations.ITEM))
            })
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (ticketState.compareTo("Archived") == 0 || ticketState.compareTo("Refused") == 0) return@apply
            if (!isChild) add(Actions.createComment(ticketId))
        },
        links = listOf(
            Links.self(Uris.makePagination(collection.pageIndex, Comments.makeBase(ticketId))),
            Links.pagination(COMMENTS_PAGINATION),
        )
    )

    fun updateCommentRepresentation(ticketId: Long, comment: CommentItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            links = listOf(Links.self(Comments.makeBase(ticketId)))
        )
    )

    fun createCommentRepresentation(ticketId: Long, comment: CommentItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            links = listOf(Links.self(Comments.makeBase(ticketId)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Comments.makeBase(ticketId))
    )

    fun deleteCommentRepresentation(ticketId: Long, comment: CommentItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            links = listOf(
                Links.self(Comments.makeSpecific(ticketId, comment.id)),
                Links.comments(ticketId)
            )
        )
    )
}