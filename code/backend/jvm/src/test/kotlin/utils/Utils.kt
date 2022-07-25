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
import pt.isel.ps.project.model.person.PersonItemDto
import pt.isel.ps.project.model.person.PersonsDto
import pt.isel.ps.project.model.room.RoomDeviceDto
import pt.isel.ps.project.model.room.RoomDto
import pt.isel.ps.project.model.room.RoomItemDto
import pt.isel.ps.project.model.room.RoomsDto
import pt.isel.ps.project.model.ticket.TicketDto
import pt.isel.ps.project.model.ticket.TicketExtraInfo
import java.io.File
import java.util.*

object Utils {
    object LoadScript {
        private val classLoader = javaClass.classLoader
        fun getResourceFile(resourceName: String) =
            File(classLoader.getResource(resourceName)!!.toURI()).readText()
    }
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