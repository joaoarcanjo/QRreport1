package pt.isel.ps.project.service

import org.springframework.stereotype.Service
import pt.isel.ps.project.dao.CategoryDao
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InternalServerException
import pt.isel.ps.project.model.category.CATEGORY_REP
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDtoItem
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.util.Validator.Category.verifyCategoryInput
import pt.isel.ps.project.util.deserializeJsonTo

@Service
class CategoryService(val categoryDao: CategoryDao) {

    fun getCategories(): CategoriesDto {
        return categoryDao.getCategories().deserializeJsonTo()
    }

    fun createCategory(category: InputCategoryEntity): CategoryDtoItem {
        verifyCategoryInput(category)
        return categoryDao.createCategory(category).getString(CATEGORY_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun updateCategory(categoryId: Long, category: InputCategoryEntity): CategoryDtoItem {
        verifyCategoryInput(category)
        return categoryDao.updateCategory(categoryId, category).getString(CATEGORY_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun activateCategory(categoryId: Long): CategoryDtoItem {
        return categoryDao.activateCategory(categoryId).getString(CATEGORY_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }

    fun deactivateCategory(categoryId: Long): CategoryDtoItem {
        return categoryDao.deactivateCategory(categoryId).getString(CATEGORY_REP)
            ?.deserializeJsonTo()
            ?: throw InternalServerException(Errors.InternalServerError.Message.INTERNAL_ERROR)
    }
}