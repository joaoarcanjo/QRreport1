package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.model.Uris.Categories
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDtoItem
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.CategoryResponses.CATEGORY_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.CategoryResponses.activateCategoryRepresentation
import pt.isel.ps.project.responses.CategoryResponses.createCategoryRepresentation
import pt.isel.ps.project.responses.CategoryResponses.deactivateCategoryRepresentation
import pt.isel.ps.project.responses.CategoryResponses.getCategoriesRepresentation
import pt.isel.ps.project.responses.CategoryResponses.updateCategoryRepresentation
import pt.isel.ps.project.service.CategoryService


@RestController
class CategoryController(private val service: CategoryService) {

    @GetMapping(Categories.BASE_PATH)
    fun getCategories(): ResponseEntity<QRreportJsonModel> {
        val categories = service.getCategories()
        return getCategoriesRepresentation(
            categories,
            CollectionModel(1, CATEGORY_PAGE_MAX_SIZE, categories.categoriesCollectionSize),
        )
    }

    @PostMapping(Categories.BASE_PATH)
    fun createCategory(@RequestBody category: InputCategoryEntity): ResponseEntity<QRreportJsonModel> {
        return createCategoryRepresentation(service.createCategory(category))
    }

    @PutMapping(Categories.SPECIFIC_PATH)
    fun updateCategory(@PathVariable categoryId: Long, @RequestBody category: InputCategoryEntity): ResponseEntity<QRreportJsonModel> {
        return updateCategoryRepresentation(service.updateCategory(categoryId, category))
    }

    @PutMapping(Categories.ACTIVATE_PATH)
    fun activateCategory(@PathVariable categoryId: Long): ResponseEntity<QRreportJsonModel> {
        return activateCategoryRepresentation(service.activateCategory(categoryId))
    }

    @DeleteMapping(Categories.SPECIFIC_PATH)
    fun deactivateCategory(@PathVariable categoryId: Long): ResponseEntity<QRreportJsonModel> {
        return deactivateCategoryRepresentation(service.deactivateCategory(categoryId))
    }
}