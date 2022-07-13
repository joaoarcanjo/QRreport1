package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.toSessionDto
import pt.isel.ps.project.model.Uris.Auth.LOGIN_PATH
import pt.isel.ps.project.model.Uris.Auth.LOGOUT_PATH
import pt.isel.ps.project.model.Uris.Auth.SIGNUP_PATH
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links
import pt.isel.ps.project.util.Validator.Auth.Roles.isAdmin
import pt.isel.ps.project.util.Validator.Auth.Roles.isEmployee
import pt.isel.ps.project.util.Validator.Auth.Roles.isManager
import pt.isel.ps.project.util.Validator.Auth.Roles.isUser

object AuthenticationResponses {
    object Actions {
        fun signup() = QRreportJsonModel.Action(
            name = "signup",
            title = "Signup",
            method = HttpMethod.POST,
            href = SIGNUP_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("phone", "string", required = false),
                QRreportJsonModel.Property("email", "string"),
                QRreportJsonModel.Property("password", "string"),
                QRreportJsonModel.Property("confirmPassword", "string"),
            )
        )

        fun login() = QRreportJsonModel.Action(
            name = "login",
            title = "Login",
            method = HttpMethod.POST,
            href = LOGIN_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("email", "string"),
                QRreportJsonModel.Property("password", "string"),
                QRreportJsonModel.Property("rememberMe", "boolean"),
            )
        )

        fun logout() = QRreportJsonModel.Action(
            name = "logout",
            title = "Logout",
            method = HttpMethod.POST,
            href = LOGOUT_PATH
        )
    }

    fun loginSignupResponse(user: AuthPerson, selfHref: String) = buildResponse(QRreportJsonModel(
        clazz = listOf(Classes.AUTH),
        properties = user.toSessionDto(),
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            add(Actions.logout())
        },
        links = mutableListOf<QRreportJsonModel.Link>().apply {
            add(Links.self(selfHref))
            if (isUser(user) || isEmployee(user) || isManager(user) || isAdmin(user)) {
                add(Links.tickets())
                add(Links.profile(user.id))
            }
            if (isAdmin(user)) {
                add(Links.devices())
                add(Links.categories())
            }
            if (isManager(user) || isAdmin(user)) {
                add(Links.companies())
                add(Links.persons())
            }
        }
    ))

    fun logoutResponse() = buildResponse(QRreportJsonModel(
        clazz = listOf(Classes.AUTH),
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            add(Actions.login())
        },
        links = mutableListOf<QRreportJsonModel.Link>().apply {
            add(Links.self(LOGOUT_PATH))
        }
    ))
}