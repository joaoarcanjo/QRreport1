package pt.isel.ps.project.model.category

import java.sql.Timestamp


data class CategoryDtoItem (
    val id: Long,
    val name: String,
    val state: String,
    val timestamp: Timestamp
)

data class CategoriesDto (
    val categories: List<CategoryDtoItem>?,
    val categoriesCollectionSize: Int
)
