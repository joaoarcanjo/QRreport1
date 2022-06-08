package pt.isel.ps.project.model.category

/*
 * Name of the category representation output parameter
 */
const val CATEGORY_REP = "categoryRep"

object CategoryEntity {
    const val CATEGORY_NAME = "name"
    const val CATEGORY_NAME_MAX_CHARS = 50
}

data class InputCategoryEntity(
    val name: String
)