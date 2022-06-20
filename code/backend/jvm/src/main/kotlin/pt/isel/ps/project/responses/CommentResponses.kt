package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
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

object CommentResponses {
    const val COMMENT_MAX_PAGE_SIZE = 10

    object Actions {
        fun createComment(ticketId: Long) = QRreportJsonModel.Action(
            name = "create-comment",
            title = "Create new comment",
            method = HttpMethod.POST,
            href = Uris.Tickets.Comments.makeBase(ticketId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("comment", "string")
            )
        )

        fun updateComment(ticketId: Long, commentId: Long) = QRreportJsonModel.Action(
            name = "update-comment",
            title = "Update comment",
            method = HttpMethod.PUT,
            href = Uris.Tickets.Comments.makeSpecific(ticketId, commentId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("comment", "string")
            )
        )

        fun deleteComment(ticketId: Long, commentId: Long) = QRreportJsonModel.Action(
            name = "delete-comment",
            title = "Delete comment",
            method = HttpMethod.DELETE,
            href = Uris.Tickets.Comments.makeSpecific(ticketId, commentId)
        )
    }

    private fun getCommentItem(ticketId: Long, ticketState: String, commentDto: CommentDto, rel: List<String>?): QRreportJsonModel {
        val comment = commentDto.comment
        return QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            rel = rel,
            properties = comment,
            entities = listOf(getPersonItem(commentDto.person, listOf(Relations.COMMENT_AUTHOR))),
            actions = mutableListOf<QRreportJsonModel.Action>().apply {
                if (ticketState.compareTo("Archived") == 0) return@apply
                add(Actions.deleteComment(ticketId, comment.id))
                add(Actions.updateComment(ticketId, comment.id))
            },
            links = listOf(Links.self(Uris.Tickets.Comments.makeSpecific(ticketId, comment.id)))
        )
    }

    fun getCommentsRepresentation(
        commentsDto: CommentsDto,
        ticketId: Long,
        ticketState: String,
        collection: CollectionModel,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.COMMENT, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (commentsDto.comments != null) addAll(commentsDto.comments.map {
                getCommentItem(ticketId, ticketState, it, listOf(Relations.ITEM))
            })
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (ticketState.compareTo("Archived") == 0) return@apply
            add(Actions.createComment(ticketId))
        },
        links = listOf(Links.self(Uris.Tickets.Comments.makeBase(ticketId)), Links.tickets())
    )

    fun getCommentRepresentation(ticketId: Long, commentDto: CommentDto): QRreportJsonModel {
        val comment = commentDto.comment
        return QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            entities = listOf(getPersonItem(commentDto.person, listOf(Relations.COMMENT_AUTHOR))),
            actions = listOf(
                Actions.deleteComment(ticketId, comment.id),
                Actions.updateComment(ticketId, comment.id)
            ),
            links = listOf(
                Links.self(Uris.Tickets.Comments.makeSpecific(ticketId, comment.id)),
                Links.comments(ticketId),
                Links.ticket(ticketId)
            )
        )
    }

    fun updateCommentRepresentation(ticketId: Long, comment: CommentItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            links = listOf(Links.self(Uris.Tickets.Comments.makeSpecific(ticketId, comment.id)))
        )
    )

    fun createCommentRepresentation(ticketId: Long, comment: CommentItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            links = listOf(Links.self(Uris.Tickets.Comments.makeSpecific(ticketId, comment.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Tickets.Comments.makeSpecific(ticketId, comment.id))
    )

    fun deleteCommentRepresentation(ticketId: Long, comment: CommentItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.COMMENT),
            properties = comment,
            links = listOf(
                Links.self(Uris.Tickets.Comments.makeSpecific(ticketId, comment.id)),
                Links.comments(ticketId)
            )
        )
    )
}