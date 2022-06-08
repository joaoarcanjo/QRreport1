package pt.isel.ps.project.controller

import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Categories
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDtoItem
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.service.CategoryService


@RestController
class CategoryController(private val service: CategoryService) {

    @GetMapping(Categories.BASE_PATH)
    fun getCategories(): CategoriesDto {
        return service.getCategories()
    }

    @PostMapping(Categories.BASE_PATH)
    fun createCategory(@RequestBody category: InputCategoryEntity): CategoryDtoItem {
        return service.createCategory(category)
    }

    @PutMapping(Categories.SPECIFIC_PATH)
    fun updateCategory(@PathVariable categoryId: Long, @RequestBody category: InputCategoryEntity): CategoryDtoItem {
        return service.updateCategory(categoryId, category)
    }

    @PutMapping(Categories.ACTIVATE_PATH)
    fun activateCategory(@PathVariable categoryId: Long): CategoryDtoItem {
        return service.activateCategory(categoryId)
    }

    @DeleteMapping(Categories.SPECIFIC_PATH)
    fun deactivateCategory(@PathVariable categoryId: Long): CategoryDtoItem {
        return service.deactivateCategory(categoryId)
    }
}