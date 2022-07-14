package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.comment.COMMENT_REP
import pt.isel.ps.project.model.comment.CreateCommentEntity
import pt.isel.ps.project.responses.CommentResponses.COMMENT_PAGE_MAX_SIZE
import java.util.*

interface CommentDao {

    @SqlQuery("SELECT get_comments(:ticketId, null, $COMMENT_PAGE_MAX_SIZE, :skip);")
    fun getComments(ticketId: Long, skip: Int): String

    @OutParameter(name = COMMENT_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL create_comment(:$COMMENT_REP, :personId, :ticketId, :comment);")
    fun createComment(ticketId: Long, @BindBean comment: CreateCommentEntity, personId: UUID): OutParameters

    @SqlQuery("SELECT get_comment(:commentId, :ticketId);")
    fun getComment(ticketId: Long, commentId: Long): String

    @OutParameter(name = COMMENT_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL update_comment(:$COMMENT_REP, :commentId, :personId, :ticketId, :comment);")
    fun updateComment(commentId: Long, personId: UUID, ticketId: Long, @BindBean comment: CreateCommentEntity): OutParameters

    @OutParameter(name = COMMENT_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL delete_comment(:$COMMENT_REP, :commentId, :personId, :ticketId);")
    fun deleteComment(commentId: Long, personId: UUID, ticketId: Long): OutParameters
}