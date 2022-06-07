package pt.isel.ps.project.model.comment

/*
 * Name of the comment representation output parameter
 */
const val COMMENT_REP = "commentRep"

object CommentEntity {
    const val COMMENT = "comment"
    const val COMMENT_MAX_CHARS = 200
}

data class InputCommentEntity(
    val comment: String,
)