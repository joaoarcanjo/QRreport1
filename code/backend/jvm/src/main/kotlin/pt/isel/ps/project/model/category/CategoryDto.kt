package pt.isel.ps.project.model.category


data class CategoryDtoItem (
    val id: Long,
    val name: String,
    val state: String
)

data class CategoriesDto (
    val categories: List<CategoryDtoItem>?,
    val categoriesCollectionSize: Int
)
