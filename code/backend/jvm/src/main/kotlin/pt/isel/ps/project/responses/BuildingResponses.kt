package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.building.BuildingDto
import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.building.BuildingManagerDto
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.PersonResponses.getPersonItem
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.responses.Response.Relations
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.setLocationHeader
import pt.isel.ps.project.responses.RoomResponses.ROOM_PAGE_MAX_SIZE
import pt.isel.ps.project.responses.RoomResponses.getRoomsRepresentation

object BuildingResponses {
    const val BUILDING_MAX_PAGE_SIZE = 10

    object Actions {
        fun createBuilding(companyId: Long) = QRreportJsonModel.Action(
            name = "create-building",
            title = "Create building",
            method = HttpMethod.POST,
            href = Uris.Companies.Buildings.makeBase(companyId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("floors", "number"),
                QRreportJsonModel.Property("managerId", "string")
            )
        )

        fun updateBuilding(companyId: Long, buildingId: Long) = QRreportJsonModel.Action(
            name = "update-building",
            title = "Update building",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.makeSpecific(companyId, buildingId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("floors", "number")
            )
        )

        fun activateBuilding(companyId: Long, buildingId: Long) = QRreportJsonModel.Action(
            name = "activate-building",
            title = "Activate building",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.makeSpecific(companyId, buildingId)
        )

        fun deactivateBuilding(companyId: Long, buildingId: Long) = QRreportJsonModel.Action(
            name = "deactivate-building",
            title = "Deactivate building",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.makeSpecific(companyId, buildingId)
        )

        fun changeBuildingManager(companyId: Long, buildingId: Long) = QRreportJsonModel.Action(
            name = "change-building-manager",
            title = "Change building manager",
            method = HttpMethod.PUT,
            href = Uris.Companies.Buildings.makeManager(companyId, buildingId),
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(QRreportJsonModel.Property("managerId", "string", required = true))
        )
    }

    fun getBuildingItem (companyId: Long, building: BuildingItemDto, rel: List<String>?) = QRreportJsonModel(
        clazz = listOf(Classes.BUILDING),
        rel = rel,
        properties = building,
        links = listOf(Links.self(Uris.Companies.Buildings.makeSpecific(companyId, building.id)))
    )

    fun getBuildingsRepresentation(
        buildings: List<BuildingItemDto>?,
        companyId: Long,
        collection: CollectionModel,
        rel: List<String>?
    ) = QRreportJsonModel(
        clazz = listOf(Classes.BUILDING, Classes.COLLECTION),
        rel = rel,
        properties = collection,
        entities = mutableListOf<QRreportJsonModel>().apply {
            if (buildings != null) addAll(buildings.map {
                getBuildingItem(companyId, it, listOf(Relations.ITEM))
            })
        },
        actions = listOf(Actions.createBuilding(companyId)),
        links = listOf(Links.self(Uris.Companies.Buildings.makeBase(companyId)), Links.tickets())
    )

    fun getBuildingRepresentation(companyId: Long, buildingDto: BuildingDto): ResponseEntity<QRreportJsonModel> {
        val building = buildingDto.building
        return buildResponse(
            QRreportJsonModel(
                clazz = listOf(Classes.BUILDING),
                properties = building,
                entities = mutableListOf<QRreportJsonModel>().apply {
                    add(getRoomsRepresentation(
                        buildingDto.rooms,
                        companyId,
                        building.id,
                        CollectionModel(
                            0,
                            if (ROOM_PAGE_MAX_SIZE < buildingDto.rooms.roomsCollectionSize)
                                ROOM_PAGE_MAX_SIZE
                            else
                                buildingDto.rooms.roomsCollectionSize
                            , buildingDto.rooms.roomsCollectionSize),
                        listOf(Relations.BUILDING_ROOMS)))
                    add(getPersonItem(buildingDto.manager, listOf(Relations.BUILDING_MANAGER)))
                },
                actions = listOf(
                    Actions.deactivateBuilding(companyId, building.id),
                    Actions.activateBuilding(companyId, building.id),
                    Actions.updateBuilding(companyId, building.id),
                    Actions.changeBuildingManager(companyId, building.id)
                ),
                links = listOf(
                    Links.self(Uris.Companies.Buildings.makeSpecific(companyId, building.id)),
                    Links.buildings(companyId),
                    Links.company(companyId)
                )
            )
        )
    }

    fun updateBuildingRepresentation(companyId: Long, building: BuildingItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.BUILDING),
            properties = building,
            links = listOf(Links.self(Uris.Companies.Buildings.makeSpecific(companyId, building.id)))
        )
    )

    fun createBuildingRepresentation(companyId: Long, building: BuildingItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.BUILDING),
            properties = building,
            links = listOf(Links.self(Uris.Companies.Buildings.makeSpecific(companyId, building.id)))
        ),
        HttpStatus.CREATED,
        setLocationHeader(Uris.Companies.Buildings.makeSpecific(companyId, building.id))
    )

    fun deactivateBuildingRepresentation(companyId: Long, building: BuildingItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.BUILDING),
            properties = building,
            links = listOf(
                Links.self(Uris.Companies.Buildings.makeSpecific(companyId, building.id)),
                Links.buildings(companyId) //todo: alterar na documentação, o rel está companies em vez de buildings
            )
        )
    )

    fun activateBuildingRepresentation(companyId: Long, building: BuildingItemDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.BUILDING),
            properties = building,
            links = listOf(Links.self(Uris.Companies.Buildings.makeSpecific(companyId, building.id)))
        )
    )

    fun changeBuildingManagerRepresentation(companyId: Long, buildingManager: BuildingManagerDto) = buildResponse(
        QRreportJsonModel(
            clazz = listOf(Classes.BUILDING),
            properties = buildingManager,
            links = listOf(Links.self(Uris.Companies.Buildings.makeSpecific(companyId, buildingManager.id)))
        )
    )
}