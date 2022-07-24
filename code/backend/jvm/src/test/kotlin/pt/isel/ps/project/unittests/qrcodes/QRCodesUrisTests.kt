package pt.isel.ps.project.unittests.qrcodes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.VERSION

class QRCodesUrisTests {

    @Test
    fun `Make valid qrcode specific path`() {
        val companyId = 123L
        val buildingId = 123L
        val roomId = 123L
        val deviceId = 123L
        val expectedPath = "$VERSION/companies/123/buildings/123/rooms/123/devices/123/qrcode"

        val path = Uris.QRCode.makeSpecific(companyId, buildingId, roomId, deviceId)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }

    @Test
    fun `Make valid qrcode report path`() {
        val hash = "5abd4089b7921fd6af09d1cc1cbe5220"
        val expectedPath = "$VERSION/report/5abd4089b7921fd6af09d1cc1cbe5220"

        val path = Uris.QRCode.makeReport(hash)

        Assertions.assertThat(path).isEqualTo(expectedPath)
    }
}