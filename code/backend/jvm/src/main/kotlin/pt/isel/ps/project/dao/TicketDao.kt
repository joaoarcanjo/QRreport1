package pt.isel.ps.project.dao

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery
import pt.isel.ps.project.model.ticket.TICKET_REP
import pt.isel.ps.project.model.ticket.CreateTicketEntity
import pt.isel.ps.project.model.ticket.UpdateTicketEntity
import pt.isel.ps.project.model.ticket.ChangeTicketStateEntity
import pt.isel.ps.project.model.ticket.TicketRateEntity
import pt.isel.ps.project.model.ticket.TicketEmployeeEntity

interface TicketDao {

    @SqlQuery("SELECT get_tickets('0a8b83ec-7675-4467-91e5-33e933441eee', null, null, null, null, null, null, null, null);")
    fun getTickets(): String

    @SqlQuery("SELECT get_ticket(:ticketId, null, null);")
    fun getTicket(ticketId: Long): String

    @SqlCall("CALL create_ticket(:$TICKET_REP, :hash, :subject, :description, :name, :email, :phone);")
    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    fun createTicket(@BindBean ticket: CreateTicketEntity): OutParameters

    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL update_ticket(:ticketId, :$TICKET_REP, :subject, :description);")
    fun updateTicket(ticketId: Long, @BindBean ticket: UpdateTicketEntity): OutParameters

    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL delete_ticket(:ticketId, :$TICKET_REP);")
    fun deleteTicket(ticketId: Long): OutParameters

    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("Call change_ticket_state(:ticketId, :state, :$TICKET_REP);")
    fun changeTicketState(ticketId: Long, @BindBean ticket: ChangeTicketStateEntity): OutParameters

    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL add_ticket_rate('0a8b83ec-7675-4467-91e5-33e933441eee', :ticketId, :rate, :$TICKET_REP);")
    fun addTicketRate(ticketId: Long, @BindBean rate: TicketRateEntity): OutParameters

    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL set_ticket_employee(:$TICKET_REP, :employeeId, :ticketId);")
    fun setEmployee(ticketId: Long, @BindBean employee: TicketEmployeeEntity): OutParameters

    @OutParameter(name = TICKET_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL remove_ticket_employee(:ticketId, :$TICKET_REP);")
    fun removeEmployee(ticketId: Long): OutParameters
}