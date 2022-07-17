package pt.isel.ps.project.unittests.room

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris

class RoomUrisTests {

    @Test
    fun `Make valid room specific path`() {
        val companyId = 123L
        val buildingId = 123L
        val roomId = 123L
        val expectedPath = "${Uris.VERSION}/companies/123/buildings/123/rooms/123"

        val path = Uris.Companies.Buildings.Rooms.makeSpecific(companyId, buildingId, roomId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid room activate path`() {
        val companyId = 123L
        val buildingId = 123L
        val roomId = 123L
        val expectedPath = "${Uris.VERSION}/companies/123/buildings/123/rooms/123/activate"

        val path = Uris.Companies.Buildings.Rooms.makeActivate(companyId, buildingId, roomId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid room devices path`() {
        val companyId = 123L
        val buildingId = 123L
        val roomId = 123L
        val expectedPath = "${Uris.VERSION}/companies/123/buildings/123/rooms/123/devices"

        val path = Uris.Companies.Buildings.Rooms.makeDevices(companyId, buildingId, roomId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid room specific device path`() {
        val companyId = 123L
        val buildingId = 123L
        val roomId = 123L
        val deviceId = 321L
        val expectedPath = "${Uris.VERSION}/companies/123/buildings/123/rooms/123/devices/321"

        val path = Uris.Companies.Buildings.Rooms.makeSpecificDevice(companyId, buildingId, roomId, deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}