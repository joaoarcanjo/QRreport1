package pt.isel.ps.project.unittests.category

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.category.CategoryEntity
import pt.isel.ps.project.model.category.InputCategoryEntity
import pt.isel.ps.project.util.Validator

class CategoryValidatorTests {

    @Test
    fun `Create or update category with valid name`() {
        val category = InputCategoryEntity("Name test")

        Assertions.assertThat(Validator.Category.verifyCategoryInput(category)).isTrue
    }

    @Test
    fun `Throws exception when category is created or updated with an blank name`() {
        val category = InputCategoryEntity("   ")

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    CategoryEntity.CATEGORY_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Category.verifyCategoryInput(category) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when category is created or updated with an invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"
        val category = InputCategoryEntity(name)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    CategoryEntity.CATEGORY_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Category.INVALID_CATEGORY_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Category.verifyCategoryInput(category) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}