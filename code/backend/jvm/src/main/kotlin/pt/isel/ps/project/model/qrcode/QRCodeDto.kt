package pt.isel.ps.project.model.qrcode

/*
 * Name of the building representation output parameter
 */
const val QRCODE_REP = "qrcodeRep"

data class QRCodeItem(
    val qrcode: String,
)

data class QRCodeDto(
    val company: String,
    val building: String,
    val room: String,
    val device: String,
    val deviceId: Long
)