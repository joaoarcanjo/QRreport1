package pt.isel.ps.project.model.device


/*
 * Name of the company representation output parameter
 */
const val DEVICE_REP = "deviceRep"

object DeviceEntity {
    const val DEVICE_NAME = "name"
    const val DEVICE_NAME_MAX_CHARS = 50
}

data class CreateDeviceEntity(
    val name: String,
    val categoryId: Int
)

data class UpdateDeviceEntity(
    val name: String
)

data class ChangeDeviceCategoryEntity (
    val newCategoryId: Int
)