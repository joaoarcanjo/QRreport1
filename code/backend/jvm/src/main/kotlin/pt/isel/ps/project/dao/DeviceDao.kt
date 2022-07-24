package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.transaction.Transaction
import pt.isel.ps.project.model.device.ChangeDeviceCategoryEntity
import pt.isel.ps.project.model.device.CreateDeviceEntity
import pt.isel.ps.project.model.device.DEVICE_REP
import pt.isel.ps.project.model.device.UpdateDeviceEntity
import pt.isel.ps.project.responses.DeviceResponses.DEVICES_PAGE_MAX_SIZE

interface DeviceDao {
    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlQuery("SELECT get_devices($DEVICES_PAGE_MAX_SIZE, :skip);")
    fun getDevices(skip: Int): String

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlQuery("SELECT get_device(:deviceId);")
    fun getDevice(deviceId: Long): String

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL create_device(:$DEVICE_REP, :name, :category);")
    fun createDevice(@BindBean device: CreateDeviceEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL update_device(:$DEVICE_REP, :deviceId, :name);")
    fun updateDevice(deviceId: Long, @BindBean device: UpdateDeviceEntity): OutParameters

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL change_device_category(:$DEVICE_REP, :deviceId, :newCategoryId);")
    fun changeDeviceCategory(deviceId: Long, @BindBean device: ChangeDeviceCategoryEntity): OutParameters

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL deactivate_device(:$DEVICE_REP, :deviceId);")
    fun deactivateDevice(deviceId: Long): OutParameters

    @Transaction(TransactionIsolationLevel.READ_COMMITTED)
    @OutParameter(name = DEVICE_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL activate_device(:$DEVICE_REP, :deviceId);")
    fun activateDevice(deviceId: Long): OutParameters

    @Transaction(TransactionIsolationLevel.SERIALIZABLE)
    @SqlQuery("SELECT get_room_devices(:companyId, :buildingId, :roomId, $DEVICES_PAGE_MAX_SIZE, :skip);")
    fun getRoomDevices(companyId: Long, buildingId: Long, roomId: Long, skip: Int): String

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlQuery("SELECT get_room_device(:companyId, :buildingId, :roomId, :deviceId);")
    fun getRoomDevice(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): String
}