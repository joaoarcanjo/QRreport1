package pt.isel.ps.project.unittests.device

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION

class DeviceUrisTests {

    @Test
    fun `Make valid device specific path`() {
        val deviceId = 123L
        val expectedPath = "$VERSION/devices/123"

        val path = Uris.Devices.makeSpecific(deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid device activate path`() {
        val deviceId = 123L
        val expectedPath = "$VERSION/devices/123/activate"

        val path = Uris.Devices.makeActivate(deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid device deactivate path`() {
        val deviceId = 123L
        val expectedPath = "$VERSION/devices/123/deactivate"

        val path = Uris.Devices.makeDeactivate(deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid device category path`() {
        val deviceId = 123L
        val expectedPath = "$VERSION/devices/123/category"

        val path = Uris.Devices.makeCategory(deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}