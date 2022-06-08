package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.category.CATEGORY_REP
import pt.isel.ps.project.model.category.InputCategoryEntity

interface CategoryDao {

    @SqlQuery("SELECT get_categories(null, null);") // :limit, :offset
    fun getCategories(): String

    @SqlCall("CALL create_category(:name, :$CATEGORY_REP);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun createCategory(@BindBean category: InputCategoryEntity): OutParameters

    @SqlCall("CALL update_category(:categoryId, :name, :$CATEGORY_REP);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun updateCategory(categoryId: Long, @BindBean category: InputCategoryEntity): OutParameters

    @SqlCall("CALL activate_category(:categoryId, :$CATEGORY_REP);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun activateCategory(categoryId: Long): OutParameters

    @SqlCall("CALL deactivate_category(:categoryId, :$CATEGORY_REP);")
    @OutParameter(name = CATEGORY_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateCategory(categoryId: Long): OutParameters
}