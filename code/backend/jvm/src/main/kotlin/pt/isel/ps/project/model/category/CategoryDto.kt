package pt.isel.ps.project.model.category

import java.sql.Timestamp

data class CategoryDto (
    val id: Long,
    val name: String,
    val state: String,
    val timestamp: Timestamp,
)

data class CategoryDtoItem (
    val category: CategoryDto,
    val inUse: Boolean?,
)

data class CategoriesDto (
    val categories: List<CategoryDtoItem>?,
    val categoriesCollectionSize: Int
)
