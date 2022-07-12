package pt.isel.ps.project.responses

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.makePagination
import pt.isel.ps.project.model.representations.CollectionModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import java.net.URI

object Response {
    object Classes {
        const val COLLECTION = "collection"
        const val COMPANY = "company"
        const val ANOMALY = "anomaly"
        const val COMMENT = "comment"
        const val TICKET = "ticket"
        const val PERSON = "person"
        const val BUILDING = "building"
        const val ROOM = "room"
        const val DEVICE = "device"
        const val QRCODE = "qrcode"
        const val CATEGORY = "category"
        const val CATEGORIES = "categories"
    }

    object Relations {
        const val SELF = "self"
        const val NEXT = "next"
        const val FIRST = "first"
        const val LAST = "last"
        const val PREV = "prev"
        const val ITEM = "item"
        const val AUTHOR = "author"
        const val PAGINATION = "pagination"
        const val COMPANIES = "companies"
        const val COMPANY_BUILDINGS = "company-buildings"
        const val DEVICE_ANOMALIES ="device-anomalies"
        const val COMMENT_AUTHOR = "comment-author"
        const val TICKET_COMPANY = "ticket-company"
        const val TICKET_BUILDING = "ticket-building"
        const val TICKET_ROOM = "ticket-room"
        const val TICKET_DEVICE = "ticket-device"
        const val TICKET_AUTHOR = "ticket-author"
        const val TICKET_COMMENTS = "ticket-comments"
        const val TICKET_EMPLOYEE = "ticket-employee"
        const val TICKETS = "tickets"
        const val COMMENTS = "comments"
        const val DEVICES = "devices"
        const val BUILDINGS = "buildings"
        const val COMMENT_TICKET = "comment-ticket"
        const val ROOM_DEVICE_QRCODE = "room-device-qrcode"
        const val BUILDING_MANAGER = "building-manager"
        const val BUILDING_ROOMS = "building-rooms"
        const val COMPANY = "company"
        const val ROOM_DEVICES = "room-devices"
        const val ROOMS = "rooms"
        const val ROOM = "room"
        const val ROOM_DEVICE = "room-device"
        const val ROOM_DEVICE_REMOVED = "room-device-removed"
        const val CATEGORIES = "categories"
    }

    object Links {
        fun self(href: String) = QRreportJsonModel.Link(listOf(Relations.SELF), href)
        fun companies() = QRreportJsonModel.Link(listOf(Relations.COMPANIES), Uris.Companies.BASE_PATH)
        fun company(companyId: Long) = QRreportJsonModel.Link(listOf(Relations.COMPANY), Uris.Companies.makeSpecific(companyId))
        fun anomalies(deviceId: Long) = QRreportJsonModel.Link(listOf(Relations.DEVICE_ANOMALIES), Uris.Devices.Anomalies.makeBase(deviceId))
        fun tickets() = QRreportJsonModel.Link(listOf(Relations.TICKETS), Uris.Tickets.BASE_PATH)
        fun ticket(ticketId: Long) = QRreportJsonModel.Link(listOf(Relations.COMMENT_TICKET), Uris.Tickets.makeSpecific(ticketId))
        fun comments(ticketId: Long) = QRreportJsonModel.Link(listOf(Relations.COMMENTS), Uris.Tickets.Comments.makeBase(ticketId))
        fun devices() = QRreportJsonModel.Link(listOf(Relations.DEVICES), Uris.Devices.BASE_PATH)
        fun roomDevices(companyId: Long, buildingId: Long, roomId: Long) =
            QRreportJsonModel.Link(listOf(Relations.DEVICES), Uris.Companies.Buildings.Rooms.makeDevices(companyId, buildingId, roomId))
        fun buildings(companyId: Long) = QRreportJsonModel.Link(listOf(Relations.BUILDINGS), Uris.Companies.Buildings.makeBase(companyId))
        fun rooms(companyId: Long, buildingId: Long) = QRreportJsonModel.Link(
            listOf(Relations.ROOMS), Uris.Companies.Buildings.Rooms.makeBase(companyId, buildingId))
        fun room(companyId: Long, buildingId: Long, roomId: Long) = QRreportJsonModel.Link(
            listOf(Relations.ROOM), Uris.Companies.Buildings.Rooms.makeSpecific(companyId, buildingId, roomId))
        fun categories() = QRreportJsonModel.Link(listOf(Relations.CATEGORIES), Uris.Categories.BASE_PATH)
        fun pagination(href: String) = QRreportJsonModel.Link(listOf(Relations.PAGINATION), href, templated = true)
    }

    fun buildResponse(
        representation: QRreportJsonModel,
        status: HttpStatus = HttpStatus.OK,
        headers: HttpHeaders = HttpHeaders(),
    ): ResponseEntity<QRreportJsonModel> {
        return ResponseEntity
            .status(status)
            .headers(headers)
            .body(representation)
    }

    fun setLocationHeader(uri: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.location = URI(uri)
        return headers
    }

    fun buildCollectionLinks(
        collection: CollectionModel,
        maxPageSize: Int,
        uri: String,
        otherLinks: List<QRreportJsonModel.Link>? = null
    ): MutableList<QRreportJsonModel.Link> {
        val links = mutableListOf<QRreportJsonModel.Link>()
        links.add(QRreportJsonModel.Link(listOf(Relations.SELF), makePagination(collection.pageIndex, uri)))
        if (collection.pageIndex * maxPageSize + maxPageSize <= collection.collectionSize) {
            links.add(QRreportJsonModel.Link(listOf(Relations.NEXT), makePagination(collection.pageIndex + 1, uri)))
            links.add(QRreportJsonModel.Link(listOf(Relations.LAST), makePagination(collection.collectionSize / maxPageSize, uri)))
        }
        if (collection.pageIndex > 1) {
            links.add(QRreportJsonModel.Link(listOf(Relations.PREV), makePagination(collection.pageIndex - 1, uri)))
            links.add(QRreportJsonModel.Link(listOf(Relations.FIRST), makePagination(0, uri)))
        }
        if (otherLinks != null) {
            links.addAll(otherLinks)
        }
        return links
    }
}
