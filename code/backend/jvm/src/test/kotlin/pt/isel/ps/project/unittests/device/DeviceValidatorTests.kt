package pt.isel.ps.project.unittests.device

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.DeviceEntity
import pt.isel.ps.project.model.device.UpdateDeviceEntity
import pt.isel.ps.project.util.Validator

class DeviceValidatorTests {

    @Test
    fun `Create device with valid name`() {
        val device = CreateDeviceEntity("ISEL", 1)

        Assertions.assertThat(Validator.Device.verifyCreateDeviceInput(device)).isTrue
    }

    @Test
    fun `Throws exception when device is created with an blank name`() {
        val device = CreateDeviceEntity("   ", 1)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    DeviceEntity.DEVICE_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Device.verifyCreateDeviceInput(device) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when device is created with an invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"
        val device = CreateDeviceEntity(name, 1)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    DeviceEntity.DEVICE_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Device.INVALID_DEVICE_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Device.verifyCreateDeviceInput(device) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Update device with valid name`() {
        val device = UpdateDeviceEntity("ISEL")

        Assertions.assertThat(Validator.Device.verifyUpdateDeviceInput(device)).isTrue
    }

    @Test
    fun `Throws exception when device is updated with an blank name`() {
        val device = UpdateDeviceEntity("    ")

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    DeviceEntity.DEVICE_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Device.verifyUpdateDeviceInput(device) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when device is updated with an invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"
        val device = UpdateDeviceEntity(name)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    DeviceEntity.DEVICE_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Device.INVALID_DEVICE_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Device.verifyUpdateDeviceInput(device) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}