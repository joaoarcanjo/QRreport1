package utils

import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.building.BuildingDto
import pt.isel.ps.project.model.building.BuildingItemDto
import pt.isel.ps.project.model.building.BuildingsDto
import pt.isel.ps.project.model.category.CategoriesDto
import pt.isel.ps.project.model.category.CategoryDto
import pt.isel.ps.project.model.category.CategoryItemDto
import pt.isel.ps.project.model.comment.CommentDto
import pt.isel.ps.project.model.comment.CommentItemDto
import pt.isel.ps.project.model.comment.CommentsDto
import pt.isel.ps.project.model.company.CompaniesDto
import pt.isel.ps.project.model.company.CompanyDto
import pt.isel.ps.project.model.company.CompanyItemDto
import pt.isel.ps.project.model.device.DeviceDto
import pt.isel.ps.project.model.device.DeviceItemDto
import pt.isel.ps.project.model.device.DeviceQrCodeDto
import pt.isel.ps.project.model.device.DevicesDto
import pt.isel.ps.project.model.person.PersonDetailsDto
import pt.isel.ps.project.model.person.PersonDto
import pt.isel.ps.project.model.room.RoomDeviceDto
import pt.isel.ps.project.model.room.RoomDto
import pt.isel.ps.project.model.room.RoomItemDto
import pt.isel.ps.project.model.room.RoomsDto
import pt.isel.ps.project.model.ticket.TicketDto
import pt.isel.ps.project.model.ticket.TicketExtraInfo
import java.io.File
import java.util.*

object Utils {
    const val DOMAIN = "http://localhost:"
    const val diogoAdminToken = "eyJhbGciOiJIUzUxMiJ9.eyJjb21wYW5pZXMiOlt7ImlkIjoxLCJuYW1lIjoiSVNFTCIsInN0YXRlIjoiYWN0" +
            "aXZlIiwibWFuYWdlcyI6WzFdfV0sInBob25lIjoiOTYxMTExMTExIiwibmFtZSI6IkRpb2dvIE5vdm8iLCJpZCI6IjRiMzQxZGUwLTY1Yz" +
            "AtNDUyNi04ODk4LTI0ZGU0NjNmYzMxNSIsInN0YXRlIjoiYWN0aXZlIiwiZW1haWwiOiJkaW9nb0BxcnJlcG9ydC5jb20iLCJhY3RpdmVSb" +
            "2xlIjoiYWRtaW4iLCJ0aW1lc3RhbXAiOjE2NTg4MDEwNzAyNDcsImlhdCI6MTY1ODgwMjg5MiwiZXhwIjoxNjU5Mzk0ODAwfQ.abyrx0mo" +
            "Po1B4oCaE91IcsIlX7Ft5zs_XG0XJMOvugmOVJPKtK-GVIypOIqrJBSD4IYUapfUO0DOqNucUdxhIg"

    const val franciscoUserToken = "eyJhbGciOiJIUzUxMiJ9.eyJwaG9uZSI6Ijk2NTM0NTYzNDUiLCJuYW1lIjoiRnJhbmNpc2NvIEx1ZG92aWN" +
            "vIiwiaWQiOiJiNTU1YjZmYy1iOTA0LTRiZDktOGMyYi00ODk1NzM4YTQzN2MiLCJzdGF0ZSI6ImFjdGl2ZSIsImVtYWlsIjoibHVkdmlrc" +
            "0BnbWFpbC5jb20iLCJhY3RpdmVSb2xlIjoidXNlciIsInRpbWVzdGFtcCI6MTY1ODc4OTMyNjE3MSwiaWF0IjoxNjU4ODQzOTQ4LCJleHA" +
            "iOjE2NTkzOTQ4MDB9.-d9ExQxirfAdJv1pSPuQuhB1sp8lR_X-PcfO8JZaVxhEM2xPcBby_oRvstzl_VVpgAgwHkIUmWOthV4U6tGwow"

    object LoadScript {
        private val classLoader = javaClass.classLoader
        fun getResourceFile(resourceName: String) =
            File(classLoader.getResource(resourceName)!!.toURI()).readText()
    }
}

fun String.ignoreTimestamp(): String {
    val timestampKey = ",\"timestamp\":"
    val timestampKeyLen = timestampKey.length - 1
    val timestampValLen = 13
    var body = this
    var index: Int
    while (true) {
        index = body.indexOf(timestampKey, 0)
        if (index < 0) break
        val timestamp = body.substring(index..index + timestampKeyLen + timestampValLen)
        body = body.replace(timestamp, "")
    }
    return body
}

fun String.ignoreCreationTimestamp(): String {
    val timestampKey = ",\"creationTimestamp\":"
    val timestampKeyLen = timestampKey.length - 1
    val timestampValLen = 13
    var body = this
    var index: Int
    while (true) {
        index = body.indexOf(timestampKey, 0)
        if (index < 0) break
        val timestamp = body.substring(index..index + timestampKeyLen + timestampValLen)
        body = body.replace(timestamp, "")
    }
    return body
}

fun AuthPerson.ignoreTimestamp() = AuthPerson(id, name, phone, email, activeRole, skills, companies, null, state, reason)
fun BuildingItemDto.ignoreTimestamp() = BuildingItemDto(id, name, floors, state, null)
fun CompanyDto.ignoreTimestamp(): CompanyDto {
    return CompanyDto(id, name, state, null, buildings?.ignoreTimestamps())
}
fun CompanyItemDto.ignoreTimestamp() = CompanyItemDto(id, name, state, null)
fun CompaniesDto.ignoreTimestamps() = CompaniesDto(
    companies?.map { it.ignoreTimestamp() },
    companiesCollectionSize
)

fun BuildingsDto.ignoreTimestamps() = BuildingsDto(
    buildings?.map { it.ignoreTimestamp() },
    buildingsCollectionSize,
    companyState
)
fun BuildingDto.ignoreTimestamp() = BuildingDto(building.ignoreTimestamp(), rooms.ignoreTimestamps(), manager)

fun RoomItemDto.ignoreTimestamp() = RoomItemDto(id, name, floor, state, null)
fun RoomsDto.ignoreTimestamps() = RoomsDto(
    rooms?.map { it.ignoreTimestamp() },
    roomsCollectionSize,
    buildingState
)
fun RoomDto.ignoreTimestamp() = RoomDto(room.ignoreTimestamp(), devices.ignoreTimestamps())
fun RoomDeviceDto.ignoreTimestamp() = RoomDeviceDto(room.ignoreTimestamp(), device.ignoreTimestamp())

fun DeviceDto.ignoreTimestamp() = DeviceDto(device.ignoreTimestamp(), anomalies)
fun DeviceQrCodeDto.ignoreTimestamp() = DeviceQrCodeDto(device.ignoreTimestamp(), hash)
fun DeviceItemDto.ignoreTimestamp() = DeviceItemDto(id, name, category, state, null)
fun DevicesDto.ignoreTimestamps() = DevicesDto(
    devices?.map { it.ignoreTimestamp() },
    devicesCollectionSize
)

fun CategoryDto.ignoreTimestamp() = CategoryDto(id, name, state, null)
fun CategoryItemDto.ignoreTimestamp() = CategoryItemDto(category.ignoreTimestamp(), inUse)
fun CategoriesDto.ignoreTimestamps() = CategoriesDto(
    categories?.map { it.ignoreTimestamp() },
    categoriesCollectionSize,
)

fun CommentItemDto.ignoreTimestamp() = CommentItemDto(id, comment, null)
fun CommentDto.ignoreTimestamp() = CommentDto(comment.ignoreTimestamp(), person)
fun CommentsDto.ignoreTimestamps() = CommentsDto(
    comments?.map { it.ignoreTimestamp() },
    collectionSize, ticketState, isTicketChild
)

fun PersonDto.changeToTest(id: UUID) = PersonDto(id, name, phone, email, roles, skills, companies, null, state, reason, bannedBy)
fun PersonDto.ignoreTimestamp() = PersonDto(id, name, phone, email, roles, skills, companies, null, state, reason, bannedBy)
fun PersonDetailsDto.ignoreTimestamp() = PersonDetailsDto(person.ignoreTimestamp(), personTickets)

fun TicketDto.ignoreTimestamp() = TicketDto(id, subject, description, null, employeeState, userState, rate, possibleTransitions)
fun TicketExtraInfo.ignoreTimestamp() = TicketExtraInfo(
    ticket.ignoreTimestamp(),
    ticketComments.ignoreTimestamps(),
    person,
    company.ignoreTimestamp(),
    building.ignoreTimestamp(),
    room.ignoreTimestamp(),
    device.ignoreTimestamp(),
    employee,
    parentTicket
)