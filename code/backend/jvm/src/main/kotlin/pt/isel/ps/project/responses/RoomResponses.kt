package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.model.room.*
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader

object RoomResponses {
    const val ROOM_PAGE_MAX_SIZE = 10

    object Actions {
        fun createRoom(companyId: Long, buildingId: Long) = QRreportJsonModel.Action(
            name = "create-room",
            title = "Create a room",
            method = HttpMethod.POST,
            href = Uris.Companies.Buildings.Rooms.makeBase(companyId, buildingId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("floor", "number")
            )
        )

        fun updateRoom(roomId: Long) = QRreportJsonModel.Action(
            name = "update-room",
            title = "Update room",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.Rooms.makeSpecific(roomId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string", required = true)
            )
        )

        fun activateRoom(roomId: Long) = QRreportJsonModel.Action(
            name = "activate-room",
            title = "Activate room",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.Rooms.makeSpecific(roomId)
        )

        fun deactivateRoom(roomId: Long) = QRreportJsonModel.Action(
            name = "deactivate-room",
            title = "Deactivate room",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.Rooms.makeSpecific(roomId)
        )

        fun addDeviceToRoom(roomId: Long) = QRreportJsonModel.Action(
            name = "add-device-to-room",
            title = "Add a device to the room",
            method = HttpMethod.POST,
            href = Uris.Companies.Buildings.Rooms.makeDevices(roomId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("device", "number", required = true)
            )
        )
    }

    fun getRoomItem (room: RoomItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.ROOM),
        rel = rel,
        properties = room,
        links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeSpecific(room.id)))
    )

    fun getRoomsRepresentation(
        rooms: RoomsDto,
        companyId: Long,
        buildingId: Long,
        collection: CollectionModel,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.ROOM, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (rooms.rooms != null) addAll(rooms.rooms.map {getRoomItem(it,listOf(Relations.ITEM))})
        },
        actions = listOf(Actions.createRoom(companyId, buildingId)),
        links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeBase(companyId, buildingId)))
    )

    fun getRoomRepresentation(roomDto: RoomDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = roomDto.room,
            entities = mutableListOf<QRreportJsonModel>().apply {
                add(DeviceResponses.devicesRepresentation(
                    roomDto.devices.devices,
                    CollectionModel(
                        0,
                        if (DeviceResponses.DEVICES_PAGE_MAX_SIZE < roomDto.devices.devicesCollectionSize)
                            DeviceResponses.DEVICES_PAGE_MAX_SIZE
                        else
                            roomDto.devices.devicesCollectionSize
                        , roomDto.devices.devicesCollectionSize),
                    listOf(Relations.ROOM_DEVICES),
                    listOf(Actions.addDeviceToRoom(roomDto.room.id))
                ))
            },
            actions = listOf(
                Actions.deactivateRoom(roomDto.room.id),
                Actions.activateRoom(roomDto.room.id),
                Actions.updateRoom(roomDto.room.id)),
            links = listOf(Links.self(Links.rooms(roomDto.companyId, roomDto.buildingId).href), Links.tickets())
        )
    )

    fun updateRoomRepresentation(room: RoomItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room,
            links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeSpecific(room.id)))
        )
    )

    fun createRoomRepresentation(room: RoomItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room,
            links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeSpecific(room.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Companies.Buildings.Rooms.makeSpecific(room.id)),
    )

    fun deactivateRoomRepresentation(room: RoomDeactivateDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room.room,
            links = listOf(
                Links.self(Uris.Companies.Buildings.Rooms.makeSpecific(room.room.id)),
                Links.rooms(room.companyId, room.buildingId)
            )
        )
    )

    fun activateRoomRepresentation(room: RoomItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = room,
            links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeSpecific(room.id)))
        )
    )

    fun addDeviceToRoomRepresentation(roomId: Long, roomDeviceDto: RoomDeviceDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = roomDeviceDto.room,
            entities = listOf(DeviceResponses.getDeviceItem(roomDeviceDto.device, listOf(Relations.ROOM_DEVICE))),
            links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeSpecificDevice(roomId, roomDeviceDto.device.id)))
        )
    )

    fun removeDeviceFromRoomRepresentation(roomId: Long, roomDeviceDto: RoomDeviceDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.ROOM),
            properties = roomDeviceDto.room,
            entities = listOf(DeviceResponses.getDeviceItem(roomDeviceDto.device, listOf(Relations.ROOM_DEVICE_REMOVED))),
            links = listOf(Links.self(Uris.Companies.Buildings.Rooms.makeSpecificDevice(roomId, roomDeviceDto.device.id)))
        )
    )
}