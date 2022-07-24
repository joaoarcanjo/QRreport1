package pt.isel.ps.project.unittests.persons

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.DeviceEntity
import pt.isel.ps.project.model.person.CreatePersonEntity
import pt.isel.ps.project.model.person.PersonEntity
import pt.isel.ps.project.util.Validator

class PersonValidatorTests {

    @Test
    fun `Create valid person`() {
        val person = CreatePersonEntity("user1", null, "user@gmail.pt", "pass", "user", null, null)

        Assertions.assertThat(Validator.Person.verifyCreatePersonInput(person)).isTrue
    }

    @Test
    fun `Throws exception when new employee doesn't has any skill`() {
        val employee = CreatePersonEntity("employeeInvalid", null, "user@gmail.pt", "pass", "employee", null, null)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.CREATE_EMPLOYEE_WITHOUT_SKILL,
            listOf(
                InvalidParameter(
                    PersonEntity.SKILL,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.EMPLOYEE_NULL_SKILL
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Person.verifyCreatePersonInput(employee) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}