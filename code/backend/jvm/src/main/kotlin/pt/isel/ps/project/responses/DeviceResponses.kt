package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.device.DeviceDto
import pt.isel.ps.project.model.device.DeviceItemDto
import pt.isel.ps.project.model.device.DeviceQrCodeDto
import pt.isel.ps.project.model.device.DevicesDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader

object DeviceResponses {
    const val DEVICES_PAGE_MAX_SIZE = 10

    object Actions {
        fun createDevice() = QRreportJsonModel.Action(
            name = "create-device",
            title = "Create new device",
            method = HttpMethod.POST,
            href = Uris.Devices.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("category", "number")
            )
        )

        fun updateDevice(deviceId: Long) = QRreportJsonModel.Action(
            name = "update-device",
            title = "Update device",
            method = HttpMethod.PUT,
            href = Uris.Devices.makeSpecific(deviceId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string", required = true)
            )
        )

        fun deactivateDevice(deviceId: Long) = QRreportJsonModel.Action(
            name = "deactivate-device",
            title = "Deactivate device",
            method = HttpMethod.PUT,
            href = Uris.Devices.makeSpecific(deviceId)
        )

        fun changeDeviceCategory(deviceId: Long) = QRreportJsonModel.Action(
            name = "change-device-category",
            title = "Change device category",
            method = HttpMethod.PUT,
            href = Uris.Devices.makeSpecific(deviceId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("category", "number", required = true)
            )
        )

        fun removeRoomDevice(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long) = QRreportJsonModel.Action(
            name = "remove-room-device",
            title = "Remove device",
            method = HttpMethod.DELETE,
            href = Uris.Companies.Buildings.Rooms.makeSpecificDevice(companyId, buildingId, roomId, deviceId)
        )
    }

    fun getDeviceItem (device: DeviceItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.DEVICE),
        rel = rel,
        properties = device,
        links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)))
    )

    fun devicesRepresentation(
        devices: List<DeviceItemDto>?,
        collection: CollectionModel,
        rel: List<String>?,
        actions: List<QRreportJsonModel.Action>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.DEVICE, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (devices != null) addAll(devices.map { getDeviceItem(it, listOf(Relations.ITEM)) })
        },
        actions = actions,
        links = listOf(Links.self(Uris.Devices.BASE_PATH))
    )

    fun getDevicesRepresentation(
        devices: List<DeviceItemDto>?,
        collection: CollectionModel,
    ) = devicesRepresentation( devices,collection,null,listOf(Actions.createDevice()))

    fun getDeviceRepresentation(deviceDto: DeviceDto): ResponseEntity<QRreportJsonModel> {
        val device = deviceDto.device
        return buildResponse(
            QRreportJsonModel(
                clazz = listOf(Classes.TICKET),
                properties = device,
                entities = mutableListOf<QRreportJsonModel>().apply {
                    add(AnomalyResponses.getAnomaliesRepresentation(
                        deviceDto.anomalies,
                        device.id,
                        CollectionModel(
                            0,
                            if (AnomalyResponses.ANOMALY_PAGE_MAX_SIZE < deviceDto.anomalies.anomaliesCollectionSize)
                                AnomalyResponses.ANOMALY_PAGE_MAX_SIZE
                            else
                                deviceDto.anomalies.anomaliesCollectionSize
                            , deviceDto.anomalies.anomaliesCollectionSize),
                        listOf(Relations.DEVICE_ANOMALIES)))
                },
                actions = listOf(
                    Actions.deactivateDevice(device.id),
                    Actions.updateDevice(device.id),
                    Actions.changeDeviceCategory(device.id)
                ),
                links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)), Links.devices())
            )
        )
    }

    fun updateDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)))
        )
    )

    fun createDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Devices.makeSpecific(device.id))
    )

    fun deactivateDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)), Links.devices())
        )
    )

    fun activateDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)), Links.devices())
        )
    )

    fun changeDeviceCategoryRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Uris.Devices.makeSpecific(device.id)))
        )
    )

    fun getRoomDevicesRepresentation(
        devicesDto: DevicesDto,
        collection: CollectionModel,
        roomId: Long
    ) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE, Classes.COLLECTION),
            properties = collection,
            entities = mutableListOf<QRreportJsonModel>().apply {
                if (devicesDto.devices != null) addAll(devicesDto.devices.map { getDeviceItem(it, listOf(Relations.ITEM)) })
            }, // TODO
            actions = listOf(RoomResponses.Actions.addRoomDevice(1, 1, roomId)),
            links = listOf(Links.self(Uris.Devices.BASE_PATH))
        )
    )

    fun getRoomDeviceRepresentation(roomId: Long, roomDeviceDto: DeviceQrCodeDto) = QRreportJsonModel(
        clazz = listOf(Classes.DEVICE),
        properties = roomDeviceDto.device,
        //entities = listOf(QrCode.getQrCodeItem(roomId, roomDeviceDto.device.id, roomDeviceDto.qrcode)),
        actions = listOf(Actions.removeRoomDevice(1, 1, roomId, roomDeviceDto.device.id)),
        links = listOf( // TODO
            Links.self(Uris.Companies.Buildings.Rooms.makeSpecificDevice(1, 1, roomId, roomDeviceDto.device.id)),
            Links.roomDevices(1, 1, roomId)
        )
    )
}