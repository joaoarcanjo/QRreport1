package pt.isel.ps.project.unittests.room

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris

class RoomUrisTests {

    @Test
    fun `Make valid room specific path`() {
        val roomId = 123L
        val expectedPath = "${Uris.VERSION}/rooms/123"

        val path = Uris.Companies.Buildings.Rooms.makeSpecific(roomId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid room activate path`() {
        val roomId = 123L
        val expectedPath = "${Uris.VERSION}/rooms/123/activate"

        val path = Uris.Companies.Buildings.Rooms.makeActivate(roomId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid room devices path`() {
        val roomId = 123L
        val expectedPath = "${Uris.VERSION}/rooms/123/devices"

        val path = Uris.Companies.Buildings.Rooms.makeDevices(roomId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid room specific device path`() {
        val roomId = 123L
        val deviceId = 321L
        val expectedPath = "${Uris.VERSION}/rooms/123/devices/321"

        val path = Uris.Companies.Buildings.Rooms.makeSpecificDevice(roomId, deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}