package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.category.CATEGORY_REP
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.responses.CategoryResponses.CATEGORY_PAGE_MAX_SIZE

interface CategoryDao {

    @SqlQuery("SELECT get_categories($CATEGORY_PAGE_MAX_SIZE, :skip);")
    fun getCategories(skip: Int): String

    @SqlCall("CALL create_category(:$CATEGORY_REP, :name);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun createCategory(@BindBean category: InputCategoryEntity): OutParameters

    @SqlCall("CALL update_category(:$CATEGORY_REP, :categoryId, :name);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun updateCategory(categoryId: Long, @BindBean category: InputCategoryEntity): OutParameters

    @SqlCall("CALL activate_category(:$CATEGORY_REP, :categoryId);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun activateCategory(categoryId: Long): OutParameters

    @SqlCall("CALL deactivate_category(:$CATEGORY_REP, :categoryId);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateCategory(categoryId: Long): OutParameters
}