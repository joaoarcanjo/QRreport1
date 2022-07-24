package pt.isel.ps.project.unittests.anomaly

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION

class AnomalyUrisTests {

    @Test
    fun `Make base anomalies path`() {
        val deviceId = 123L
        val expectedPath = "$VERSION/devices/123/anomalies"

        val path = Uris.Devices.Anomalies.makeBase(deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid anomaly specific path`() {
        val deviceId = 123L
        val anomalyId = 321L
        val expectedPath = "$VERSION/devices/123/anomalies/321"

        val path = Uris.Devices.Anomalies.makeSpecific(deviceId, anomalyId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}