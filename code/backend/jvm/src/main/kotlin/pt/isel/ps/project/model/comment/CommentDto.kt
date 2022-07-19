package pt.isel.ps.project.model.comment

import pt.isel.ps.project.model.person.PersonItemDto
import java.sql.Timestamp

data class CommentDto (
    val comment: CommentItemDto,
    val person: PersonItemDto
)

data class CommentItemDto (
    val id: Long,
    val comment: String,
    val timestamp: Timestamp,
)

data class CommentsDto (
    val comments: List<CommentDto>?,
    val collectionSize: Int,
    val ticketState: String,
    val isTicketChild: Boolean,
)