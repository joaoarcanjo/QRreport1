package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transaction
import pt.isel.ps.project.model.category.CATEGORY_REP
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.responses.CategoryResponses.CATEGORY_PAGE_MAX_SIZE

interface CategoryDao {
    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlQuery("SELECT get_categories($CATEGORY_PAGE_MAX_SIZE, :skip);")
    fun getCategories(skip: Int): String

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @SqlCall("CALL create_category(:$CATEGORY_REP, :name);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun createCategory(@BindBean category: InputCategoryEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlCall("CALL update_category(:$CATEGORY_REP, :categoryId, :name);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun updateCategory(categoryId: Long, @BindBean category: InputCategoryEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlCall("CALL activate_category(:$CATEGORY_REP, :categoryId);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun activateCategory(categoryId: Long): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlCall("CALL deactivate_category(:$CATEGORY_REP, :categoryId);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateCategory(categoryId: Long): OutParameters
}