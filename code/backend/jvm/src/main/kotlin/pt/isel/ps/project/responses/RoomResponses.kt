package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris.Companies.Buildings.Rooms
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.DEFAULT_PAGE
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.responses.DeviceResponses.DEVICES_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.DeviceResponses.devicesRepresentation
import pt.isel.ps.project.responses.DeviceResponses.getDeviceItem
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Person.isBuildingManager

object RoomResponses {
    const val ROOM_PAGE_MAX_SIZE = 10

    object Actions {
        fun createRoom(companyId: Long, buildingId: Long) = QRreportJsonModel.Action(
            name = "create-room",
            title = "Create a room",
            method = HttpMethod.POST,
            href = Rooms.makeBase(companyId, buildingId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("floor", "number")
            )
        )

        fun updateRoom(companyId: Long, buildingId: Long, roomId: Long) = QRreportJsonModel.Action(
            name = "update-room",
            title = "Update room",
            method = HttpMethod.PUT,
            href = Rooms.makeSpecific(companyId, buildingId, roomId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
            )
        )

        fun activateRoom(companyId: Long, buildingId: Long, roomId: Long) = QRreportJsonModel.Action(
            name = "activate-room",
            title = "Activate room",
            method = HttpMethod.POST,
            href = Rooms.makeActivate(companyId, buildingId, roomId)
        )

        fun deactivateRoom(companyId: Long, buildingId: Long, roomId: Long) = QRreportJsonModel.Action(
            name = "deactivate-room",
            title = "Deactivate room",
            method = HttpMethod.POST,
            href = Rooms.makeDeactivate(companyId, buildingId, roomId)
        )

        fun addRoomDevice(companyId: Long, buildingId: Long, roomId: Long) = QRreportJsonModel.Action(
            name = "add-room-device",
            title = "Add device",
            method = HttpMethod.POST,
            href = Rooms.makeDevices(companyId, buildingId, roomId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("device", "number")
            )
        )
    }

    fun getRoomItem (companyId: Long, buildingId: Long, room: RoomItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.ROOM),
        rel = rel,
        properties = room,
        links = listOf(Links.self(Rooms.makeSpecific(companyId, buildingId, room.id)))
    )

    fun getRoomsRepresentation(
        user: AuthPerson,
        companyId: Long,
        buildingId: Long,
        rooms: RoomsDto,
        collection: CollectionModel,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.ROOM, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (rooms.rooms != null) addAll(rooms.rooms.map {
                getRoomItem(companyId, buildingId, it, listOf(Relations.ITEM))
            })
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (isManager(user) && !isBuildingManager(user, buildingId)) return@apply
            add(Actions.createRoom(companyId, buildingId))
        },
        links = listOf(Links.self(Rooms.makeBase(companyId, buildingId)))
    )

    fun getRoomRepresentation(
        user: AuthPerson,
        companyId: Long,
        buildingId: Long,
        roomDto: RoomDto
    ) = buildResponse(QRreportJsonModel(
        clazz = listOf(Classes.ROOM),
        properties = roomDto.room,
        entities = mutableListOf<QRreportJsonModel>().apply {
            add(
                devicesRepresentation(
                    roomDto.devices.devices,
                    CollectionModel(DEFAULT_PAGE, DEVICES_PAGE_MAX_SIZE, roomDto.devices.devicesCollectionSize),
                    listOf(Relations.ROOM_DEVICES),
                    listOf(Actions.addRoomDevice(companyId, buildingId, roomDto.room.id))
            ))
        },
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            if (isManager(user) && !isBuildingManager(user, buildingId)) return@apply
            add(Actions.deactivateRoom(companyId, buildingId, roomDto.room.id))
            add(Actions.activateRoom(companyId, buildingId, roomDto.room.id))
            add(Actions.updateRoom(companyId, buildingId, roomDto.room.id))
            add(Actions.addRoomDevice(companyId, buildingId, roomDto.room.id))
        },
        links = listOf(
            Links.self(Rooms.makeSpecific(companyId, buildingId, roomDto.room.id)),
            Links.company(companyId),
        )
    ))

    fun updateRoomRepresentation(companyId: Long, buildingId: Long, room: RoomItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room,
            links = listOf(Links.self(Rooms.makeSpecific(companyId, buildingId, room.id)))
        )
    )

    fun createRoomRepresentation(companyId: Long, buildingId: Long, room: RoomItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room,
            links = listOf(Links.self(Rooms.makeSpecific(companyId, buildingId, room.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Rooms.makeSpecific(companyId, buildingId, room.id)),
    )

    fun deactivateActivateRoomRepresentation(companyId: Long, buildingId: Long, room: RoomItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room,
            links = listOf(
                Links.self(Rooms.makeSpecific(companyId, buildingId, room.id)),
                Links.room(companyId, buildingId, room.id)
            )
        )
    )

    fun addDeviceToRoomRepresentation(companyId: Long, buildingId: Long, roomId: Long, roomDeviceDto: RoomDeviceDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = roomDeviceDto.room,
            entities = listOf(getDeviceItem(roomDeviceDto.device, listOf(Relations.ROOM_DEVICE))),
            links = listOf(Links.self(Rooms.makeSpecificDevice(companyId, buildingId, roomId, roomDeviceDto.device.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Rooms.makeDevices(companyId, buildingId, roomId)),
    )

    fun removeDeviceFromRoomRepresentation(companyId: Long, buildingId: Long, roomId: Long, roomDeviceDto: RoomDeviceDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = roomDeviceDto.room,
            entities = listOf(getDeviceItem(roomDeviceDto.device, listOf(Relations.ROOM_DEVICE_REMOVED))),
            links = listOf(
                Links.self(Rooms.makeSpecificDevice(companyId, buildingId, roomId, roomDeviceDto.device.id)),
                Links.room(companyId, buildingId, roomId)
            )
        )
    )
}