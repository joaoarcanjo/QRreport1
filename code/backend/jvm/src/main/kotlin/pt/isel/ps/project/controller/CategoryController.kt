package pt.isel.ps.project.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.Authorizations.Category.activateCategoryAuthorization
import pt.isel.ps.project.auth.Authorizations.Category.createCategoryAuthorization
import pt.isel.ps.project.auth.Authorizations.Category.deactivateCategoryAuthorization
import pt.isel.ps.project.auth.Authorizations.Category.getCategoriesAuthorization
import pt.isel.ps.project.auth.Authorizations.Category.updateCategoryAuthorization
import pt.isel.ps.project.model.Uris.Categories
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
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
    fun getCategories(@RequestParam(defaultValue = "$DEFAULT_PAGE") page: Int, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        getCategoriesAuthorization(user)
        val categories = service.getCategories(page)
        return getCategoriesRepresentation(
            user,
            categories,
            CollectionModel(page, CATEGORY_PAGE_MAX_SIZE, categories.categoriesCollectionSize),
        )
    }

    @PostMapping(Categories.BASE_PATH)
    fun createCategory(@RequestBody category: InputCategoryEntity, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        createCategoryAuthorization(user)
        return createCategoryRepresentation(service.createCategory(category))
    }

    @PutMapping(Categories.SPECIFIC_PATH)
    fun updateCategory(
        @PathVariable categoryId: Long,
        @RequestBody category: InputCategoryEntity,
        user: AuthPerson,
    ): ResponseEntity<QRreportJsonModel> {
        updateCategoryAuthorization(user)
        return updateCategoryRepresentation(service.updateCategory(categoryId, category))
    }

    @PostMapping(Categories.ACTIVATE_PATH)
    fun activateCategory(@PathVariable categoryId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        activateCategoryAuthorization(user)
        return activateCategoryRepresentation(service.activateCategory(categoryId))
    }

    @PostMapping(Categories.DEACTIVATE_PATH)
    fun deactivateCategory(@PathVariable categoryId: Long, user: AuthPerson): ResponseEntity<QRreportJsonModel> {
        deactivateCategoryAuthorization(user)
        return deactivateCategoryRepresentation(service.deactivateCategory(categoryId))
    }
}