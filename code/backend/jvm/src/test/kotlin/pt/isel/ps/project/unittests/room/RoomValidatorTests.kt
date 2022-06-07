package pt.isel.ps.project.unittests.room

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.room.CreateRoomEntity
import pt.isel.ps.project.model.room.RoomEntity
import pt.isel.ps.project.model.room.UpdateRoomEntity
import pt.isel.ps.project.util.Validator.Company.Building.Room.verifyCreateRoomInput
import pt.isel.ps.project.util.Validator.Company.Building.Room.verifyUpdateRoomInput

class RoomValidatorTests {

    @Test
    fun `Create room with valid name and floor number`() {
        val room = CreateRoomEntity("Name test", 12)

        Assertions.assertThat(verifyCreateRoomInput(room)).isTrue
    }

    @Test
    fun `Throws exception when room is created with an blank name`() {
        val room = CreateRoomEntity("   ", 12)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    RoomEntity.ROOM_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateRoomInput(room) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when room is created with invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"
        val floor = 12

        val room = CreateRoomEntity(name, floor)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    RoomEntity.ROOM_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateRoomInput(room) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when room is created with invalid floor number`() {
        val room = CreateRoomEntity("Room number", 99999)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    RoomEntity.ROOM_FLOOR,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_FLOOR_NUMBER
                )
            )
        )

        Assertions.assertThatThrownBy { verifyCreateRoomInput(room) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Update room with valid name`() {
        val room = UpdateRoomEntity("Name test")

        Assertions.assertThat(verifyUpdateRoomInput(room)).isTrue
    }

    @Test
    fun `Throws exception when room is updated with an blank name`() {
        val room = UpdateRoomEntity("   ")

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    RoomEntity.ROOM_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { verifyUpdateRoomInput(room) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when room is updated with invalid name length`() {
        val name = "012345678901234567890123456789012345678901234567890123456789"

        val room = UpdateRoomEntity(name)

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.INVALID_REQ_PARAMS,
            listOf(
                InvalidParameter(
                    RoomEntity.ROOM_NAME,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.Company.Building.Room.INVALID_ROOM_NAME_LENGTH
                )
            )
        )

        Assertions.assertThatThrownBy { verifyUpdateRoomInput(room) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}