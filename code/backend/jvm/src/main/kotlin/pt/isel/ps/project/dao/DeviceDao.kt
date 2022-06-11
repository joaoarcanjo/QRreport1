package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.company.COMPANY_REP
import pt.isel.ps.project.model.device.ChangeDeviceCategoryEntity
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.DEVICE_REP
import pt.isel.ps.project.model.device.UpdateDeviceEntity

interface DeviceDao {

    @SqlQuery("SELECT get_devices(null, null);") // :limit, :offset
    fun getDevices(): String

    @SqlQuery("SELECT get_device(:deviceId);")
    fun getDevice(deviceId: Int): String

    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL create_device(:name, :categoryId, :$DEVICE_REP);")
    fun createDevice(@BindBean device: CreateDeviceEntity): OutParameters

    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL update_device(:deviceId, :name, :$DEVICE_REP);")
    fun updateDevice(deviceId: Int, @BindBean device: UpdateDeviceEntity): OutParameters

    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL change_device_category(:deviceId, :newCategoryId, :$DEVICE_REP);")
    fun changeDeviceCategory(deviceId: Int, @BindBean device: ChangeDeviceCategoryEntity): OutParameters

    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL deactivate_device(:deviceId, :$DEVICE_REP);")
    fun deactivateDevice(deviceId: Int): OutParameters

    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL activate_device(:deviceId, :$DEVICE_REP);")
    fun activateDevice(deviceId: Int): OutParameters

    @SqlQuery("SELECT get_room_devices(:roomId, null, null);") // :limit, :offset
    fun getRoomDevices(roomId: Int): String

    @SqlQuery("SELECT get_room_device(:roomId, :deviceId);")
    fun getRoomDevice(roomId: Int, deviceId: Int): String
}