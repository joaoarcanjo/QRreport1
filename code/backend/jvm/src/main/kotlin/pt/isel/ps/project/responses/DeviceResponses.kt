package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms.makeDevices
import pt.isel.ps.project.model.Uris.Devices
import pt.isel.ps.project.model.Uris.Devices.DEVICES_PAGINATION
import pt.isel.ps.project.model.device.DeviceDto
import pt.isel.ps.project.model.device.DeviceItemDto
import pt.isel.ps.project.model.device.DeviceQrCodeDto
import pt.isel.ps.project.model.device.DevicesDto
import pt.isel.ps.project.model.qrcode.QRCodeItem
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.AnomalyResponses.ANOMALY_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Auth.States.isInactive
import pt.isel.ps.project.util.Validator.Person.isBuildingManager

object DeviceResponses {
    const val DEVICES_PAGE_MAX_SIZE = 10

    object Actions {
        fun createDevice() = QRreportJsonModel.Action(
            name = "create-device",
            title = "Create device",
            method = HttpMethod.POST,
            href = Devices.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("category", "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Categories.BASE_PATH))
            )
        )

        fun updateDevice(deviceId: Long) = QRreportJsonModel.Action(
            name = "update-device",
            title = "Update device",
            method = HttpMethod.PUT,
            href = Devices.makeSpecific(deviceId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string")
            )
        )

        fun deactivateDevice(deviceId: Long) = QRreportJsonModel.Action(
            name = "deactivate-device",
            title = "Deactivate device",
            method = HttpMethod.POST,
            href = Devices.makeDeactivate(deviceId)
        )

        fun activateDevice(deviceId: Long) = QRreportJsonModel.Action(
            name = "activate-device",
            title = "Activate device",
            method = HttpMethod.POST,
            href = Devices.makeActivate(deviceId)
        )

        fun changeDeviceCategory(deviceId: Long) = QRreportJsonModel.Action(
            name = "change-device-category",
            title = "Change device category",
            method = HttpMethod.PUT,
            href = Devices.makeCategory(deviceId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("category", "number",
                    possibleValues = QRreportJsonModel.PropertyValue(Uris.Categories.BASE_PATH))
            )
        )

        fun removeRoomDevice(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long) = QRreportJsonModel.Action(
            name = "remove-room-device",
            title = "Remove device",
            method = HttpMethod.DELETE,
            href = Rooms.makeSpecificDevice(companyId, buildingId, roomId, deviceId)
        )

        fun generateNewQRCode(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long) = QRreportJsonModel.Action(
            name = "generate-new-qrcode",
            title = "Generate new QR Code",
            method = HttpMethod.POST,
            href = Uris.QRCode.makeSpecific(companyId, buildingId, roomId, deviceId)
        )
    }

    fun getDeviceItem (device: DeviceItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.DEVICE),
        rel = rel,
        properties = device,
        links = listOf(Links.self(Devices.makeSpecific(device.id)))
    )

    fun devicesRepresentation(
        devices: List<DeviceItemDto>?,
        collection: CollectionModel,
        rel: List<String>?,
        actions: List<QRreportJsonModel.Action>?,
        linkSelf: String,
        linkPagination: String
    ) = QRreportJsonModel(
        clazz = listOf(Classes.DEVICE, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (devices != null) addAll(devices.map { getDeviceItem(it, listOf(Relations.ITEM)) })
        },
        actions = actions,
        links = listOf(
            Links.self(Uris.makePagination(collection.pageIndex, linkSelf)),
            Links.pagination(linkPagination),
        )
    )

    fun getDevicesRepresentation(
        user: AuthPerson,
        devices: List<DeviceItemDto>?,
        collection: CollectionModel,
    ) = devicesRepresentation(
        devices,
        collection,
        null,
        mutableListOf<QRreportJsonModel.Action>().apply {
            if (isAdmin(user)) add(Actions.createDevice())
        },
        Devices.BASE_PATH,
        DEVICES_PAGINATION
    )

    fun getDeviceRepresentation(user: AuthPerson, deviceDto: DeviceDto): ResponseEntity<QRreportJsonModel> {
        val device = deviceDto.device
        return buildResponse(
            QRreportJsonModel(
                clazz = listOf(Classes.TICKET),
                properties = device,
                entities = mutableListOf<QRreportJsonModel>().apply {
                    add(AnomalyResponses.getAnomaliesRepresentation(
                        user,
                        deviceDto.anomalies,
                        device.id,
                        CollectionModel(DEFAULT_PAGE, ANOMALY_PAGE_MAX_SIZE, deviceDto.anomalies.anomaliesCollectionSize),
                        listOf(Relations.DEVICE_ANOMALIES))
                    )
                },
                actions = mutableListOf<QRreportJsonModel.Action>().apply {
                    if (!isAdmin(user)) return@apply
                    if (isInactive(deviceDto.device.state))
                        add(Actions.activateDevice(device.id))
                    else {
                        add(Actions.deactivateDevice(device.id))
                        add(Actions.updateDevice(device.id))
                        add(Actions.changeDeviceCategory(device.id))
                    }
                },
                links = listOf(
                    Links.self(Devices.makeSpecific(device.id)),
                    Links.devices(),
                )
            )
        )
    }

    fun createDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Devices.makeSpecific(device.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Devices.makeSpecific(device.id))
    )

    fun updateDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Devices.makeSpecific(device.id)))
        )
    )

    fun deactivateDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(
                Links.self(Devices.makeSpecific(device.id)),
                Links.devices(),
            )
        )
    )

    fun activateDeviceRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(
                Links.self(Devices.makeSpecific(device.id)),
            )
        )
    )

    fun changeDeviceCategoryRepresentation(device: DeviceItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.DEVICE),
            properties = device,
            links = listOf(Links.self(Devices.makeSpecific(device.id)))
        )
    )

    fun getRoomDevicesRepresentation(
        user: AuthPerson,
        companyId: Long,
        buildingId: Long,
        roomId: Long,
        devicesDto: DevicesDto,
        collection: CollectionModel,
    ) = buildResponse(QRreportJsonModel(
        clazz = listOf(Classes.DEVICE, Classes.COLLECTION),
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (devicesDto.devices != null) addAll(devicesDto.devices.map { getDeviceItem(it, listOf(Relations.ITEM)) })
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (isManager(user) && !isBuildingManager(user, companyId, buildingId)) return@apply
            add(RoomResponses.Actions.addRoomDevice(companyId, buildingId, roomId))
        },
        links = listOf(
            Links.self(Devices.BASE_PATH),
            Links.pagination(Rooms.ROOM_DEVICES_PAGINATION)
        )
    ))

    fun getRoomDeviceRepresentation(
        user: AuthPerson,
        companyId: Long,
        buildingId: Long,
        roomId: Long,
        roomDeviceDto: DeviceQrCodeDto
    ) = buildResponse(QRreportJsonModel(
        clazz = listOf(Classes.DEVICE),
        properties = roomDeviceDto.device,
        entities = listOf(getQRCodeItem(user, companyId, buildingId, roomId, roomDeviceDto.device.id, roomDeviceDto.device.state)),
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (isManager(user) && !isBuildingManager(user, companyId, buildingId)) return@apply
            add(Actions.removeRoomDevice(companyId, buildingId, roomId, roomDeviceDto.device.id))
        },
        links = listOf(
            Links.self(Rooms.makeSpecificDevice(companyId, buildingId, roomId, roomDeviceDto.device.id)),
            Links.room(companyId, buildingId, roomId)
        )
    ))

    private fun getQRCodeItem(user: AuthPerson, companyId: Long, buildingId: Long, roomId: Long, deviceId: Long, deviceState: String) = QRreportJsonModel(
        clazz = listOf(Classes.QRCODE),
        rel = listOf(Relations.QRCODE),
        properties = QRCodeItem(Uris.QRCode.makeSpecific(companyId, buildingId, roomId, deviceId)),
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if ((isInactive(deviceState)) || (isManager(user) && !isBuildingManager(user, companyId, buildingId))) return@apply
            add(Actions.generateNewQRCode(companyId, buildingId, roomId, deviceId))
        },
        links = listOf()
    )
}