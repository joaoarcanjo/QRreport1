package pt.isel.ps.project.unittests.anomaly

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris

class AnomalyUrisTests {

    @Test
    fun `Make valid anomaly specific path`() {
        val deviceId = 123
        val anomalyId = 321
        val expectedPath = "${Uris.VERSION}/devices/123/anomalies/321"

        val path = Uris.Devices.Anomalies.makeSpecific(deviceId, anomalyId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}