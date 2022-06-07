package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.room.AddDeviceEntity
import pt.isel.ps.project.model.room.CreateRoomEntity
import pt.isel.ps.project.model.room.ROOM_REP
import pt.isel.ps.project.model.room.UpdateRoomEntity

interface RoomDao {

    @SqlQuery("SELECT get_rooms(:companyId, :buildingId, null, null);") // :limit, :offset
    fun getRooms(companyId: Long, buildingId: Long): String

    @SqlQuery("SELECT get_room(:roomId, null, null);")
    fun getRoom(roomId: Long): String

    @SqlCall("CALL create_room(:companyId, :buildingId, :name, :floor, :$ROOM_REP);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun createRoom(companyId: Long, buildingId: Long, @BindBean room: CreateRoomEntity): OutParameters

    @SqlCall("CALL update_room(:roomId, :name, :$ROOM_REP);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun updateRoom(roomId: Long, @BindBean room: UpdateRoomEntity): OutParameters

    @SqlCall("CALL add_room_device(:roomId, :deviceId, :$ROOM_REP);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun addRoomDevice(roomId: Long, @BindBean device: AddDeviceEntity): OutParameters

    @SqlCall("CALL remove_room_device(:roomId, :deviceId, :$ROOM_REP);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun removeRoomDevice(roomId: Long, deviceId: Long): OutParameters

    @SqlCall("CALL activate_room(:roomId, :$ROOM_REP);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun activateRoom(roomId: Long): OutParameters

    @SqlCall("CALL deactivate_room(:roomId, :$ROOM_REP);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateRoom(roomId: Long): OutParameters
}