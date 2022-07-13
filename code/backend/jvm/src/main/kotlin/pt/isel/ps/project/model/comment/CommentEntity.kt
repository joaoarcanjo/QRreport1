package pt.isel.ps.project.model.comment

import java.util.UUID

/*
 * Name of the comment representation output parameter
 */
const val COMMENT_REP = "commentRep"

object CommentEntity {
    const val COMMENT = "comment"
    const val COMMENT_MAX_CHARS = 200
}

data class CreateCommentEntity(
    val comment: String,
    //val person: UUID,
)