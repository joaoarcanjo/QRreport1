package pt.isel.ps.project.model.device

import pt.isel.ps.project.model.anomaly.AnomaliesDto
import pt.isel.ps.project.model.anomaly.AnomalyItemDto
import pt.isel.ps.project.model.qrcode.QRCodeDto
import java.sql.Timestamp

data class DeviceItemDto (
    val id: Long,
    val name: String,
    val category: String,
    val state: String,
    val timestamp: Timestamp
)

data class DeviceDto (
    val device: DeviceItemDto,
    val anomalies: AnomaliesDto
)

data class DeviceQrCodeDto (
    val device: DeviceItemDto,
    val qrcode: QRCodeDto

)

data class DevicesDto (
    val devices: List<DeviceItemDto>?,
    val devicesCollectionSize: Int,
)