package pt.isel.ps.project.unittests.company

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.company.CompanyEntity
import pt.isel.ps.project.model.company.CreateCompanyEntity
import pt.isel.ps.project.model.company.UpdateCompanyEntity
import pt.isel.ps.project.util.Validator.Company.verifyCreateCompanyInput
import pt.isel.ps.project.util.Validator.Company.verifyUpdateCompanyInput

class CompanyValidatorTests {

    @Test
    fun `Create company with valid name`() {
        val company = CreateCompanyEntity("ISEL")

        assertThat(verifyCreateCompanyInput(company)).isTrue
    }

    @Test
    fun `Throws exception when company is created with an invalid name length`() {
        val invName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaad"
        val company = CreateCompanyEntity(invName)
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    CompanyEntity.COMPANY_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.INVALID_NAME_LENGTH
                )
            )
        )

        assertThatThrownBy { verifyCreateCompanyInput(company) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Update company with valid name`() {
        val company = UpdateCompanyEntity("ISEL")

        assertThat(verifyUpdateCompanyInput(company)).isTrue
    }

    @Test
    fun `Throws exception when company is updated with an invalid name length`() {
        val invName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaad"
        val company = UpdateCompanyEntity(invName)
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    CompanyEntity.COMPANY_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.INVALID_NAME_LENGTH
                )
            )
        )

        assertThatThrownBy { verifyUpdateCompanyInput(company) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when update company entity parameters are all null`() {
        val company = UpdateCompanyEntity(null)
        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.UPDATE_NULL_PARAMS,
            detail = Errors.BadRequest.Message.UPDATE_NULL_PARAMS_DETAIL,
        )

        assertThatThrownBy { verifyUpdateCompanyInput(company) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}