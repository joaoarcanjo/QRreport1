package pt.isel.ps.project.unittests.building

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.building.BuildingEntity
import pt.isel.ps.project.model.building.CreateBuildingEntity
import pt.isel.ps.project.model.building.UpdateBuildingEntity
import pt.isel.ps.project.util.Validator
import java.util.*

class BuildingValidatorTests {

    @Test
    fun `Create building with valid building name`() {
        val building = CreateBuildingEntity("Name test", 12, UUID.randomUUID())

        Assertions.assertThat(Validator.Company.Building.verifyCreateBuildingInput(building)).isTrue
    }

    @Test
    fun `Throws exception when building is created with an blank name`() {
        val building = CreateBuildingEntity("   ", 12, UUID.randomUUID())

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    BuildingEntity.BUILDING_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Company.Building.verifyCreateBuildingInput(building) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when building is created with an invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"
        val floors = 12
        val manager = UUID.randomUUID()
        val building = CreateBuildingEntity(name, floors, manager)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    BuildingEntity.BUILDING_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Company.Building.verifyCreateBuildingInput(building) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Update building with valid name`() {
        val building = UpdateBuildingEntity("Name test v2.0", 12)

        Assertions.assertThat(Validator.Company.Building.verifyUpdateBuildingInput(building)).isTrue
    }

    @Test
    fun `Throws exception when building is updated with an blank name`() {
        val building = UpdateBuildingEntity("   ", 12)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    BuildingEntity.BUILDING_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Company.Building.verifyUpdateBuildingInput(building) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when building is updated with an invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"
        val building = CreateBuildingEntity(name, 12, UUID.randomUUID())

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    BuildingEntity.BUILDING_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.Building.INVALID_BUILDING_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Company.Building.verifyCreateBuildingInput(building) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}