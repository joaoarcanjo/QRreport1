package pt.isel.ps.project.auth

import org.jdbi.v3.core.statement.OutParameters
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.OutParameter
import org.jdbi.v3.sqlobject.statement.SqlCall
import org.jdbi.v3.sqlobject.statement.SqlQuery

interface AuthDao {
    @SqlQuery("SELECT get_credentials(:email)")
    fun getCredentials(email: String): String

    @SqlQuery("SELECT login(:email)")
    fun login(email: String): String

    @OutParameter(name = AUTH_REP, sqlType = java.sql.Types.OTHER)
    @SqlCall("CALL signup(:$AUTH_REP, :name, :phone, :email, :encPassword)")
    fun signup(@BindBean signupDto: SignupDto, encPassword: String): OutParameters
}