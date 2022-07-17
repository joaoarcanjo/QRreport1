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
import pt.isel.ps.project.responses.RoomResponses.ROOM_PAGE_MAX_SIZE

interface RoomDao {

    @SqlQuery("SELECT get_rooms(:companyId, :buildingId, $ROOM_PAGE_MAX_SIZE, :skip);")
    fun getRooms(companyId: Long, buildingId: Long, skip: Int): String

    @SqlCall("CALL create_room(:$ROOM_REP, :companyId, :buildingId, :name, :floor);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun createRoom(companyId: Long, buildingId: Long, @BindBean room: CreateRoomEntity): OutParameters

    @SqlQuery("SELECT get_room(:companyId, :buildingId, :roomId, $ROOM_PAGE_MAX_SIZE, 0);")
    fun getRoom(companyId: Long, buildingId: Long, roomId: Long): String

    @SqlCall("CALL update_room(:$ROOM_REP, :companyId, :buildingId, :roomId, :name);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun updateRoom(companyId: Long, buildingId: Long, roomId: Long, @BindBean room: UpdateRoomEntity): OutParameters

    @SqlCall("CALL deactivate_room(:$ROOM_REP, :companyId, :buildingId, :roomId);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun deactivateRoom(companyId: Long, buildingId: Long, roomId: Long): OutParameters

    @SqlCall("CALL activate_room(:$ROOM_REP, :companyId, :buildingId, :roomId);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun activateRoom(companyId: Long, buildingId: Long, roomId: Long): OutParameters

    @SqlCall("CALL add_room_device(:$ROOM_REP, :companyId, :buildingId, :roomId, :deviceId);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun addRoomDevice(companyId: Long, buildingId: Long, roomId: Long, @BindBean device: AddDeviceEntity): OutParameters

    @SqlCall("CALL remove_room_device(:$ROOM_REP, :companyId, :buildingId, :roomId, :deviceId);")
    @OutParameter(name = ROOM_REP, sqlType = java.sql.Types.OTHER)
    fun removeRoomDevice(companyId: Long, buildingId: Long, roomId: Long, deviceId: Long): OutParameters
}