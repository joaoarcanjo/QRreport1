package pt.isel.ps.project.responses

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.qrcode.QRCodeDto
import pt.isel.ps.project.model.representations.QRreportJsonModel
import pt.isel.ps.project.responses.Response.buildResponse
import pt.isel.ps.project.responses.Response.Classes
import pt.isel.ps.project.responses.Response.Links

object QRCodeResponses {
    object Actions {
        fun report() = QRreportJsonModel.Action(
            name = "report",
            title = "Submit report",
            method = HttpMethod.POST,
            href = Uris.Tickets.BASE_PATH,
            type = MediaType.APPLICATION_JSON.toString(),
            properties = listOf(
                QRreportJsonModel.Property("subject", "string"),
                QRreportJsonModel.Property("description", "string", required = false),
                QRreportJsonModel.Property("hash", "string"),
                QRreportJsonModel.Property("name", "string"),
                QRreportJsonModel.Property("phone", "string", required = false),
                QRreportJsonModel.Property("email", "string"),
            )
        )
    }

    fun getReportFormResponse(user: AuthPerson?, hash: String, qrCodeDto: QRCodeDto) = buildResponse(QRreportJsonModel(
        clazz = listOf(Classes.REPORT),
        properties = qrCodeDto,
        actions = mutableListOf<QRreportJsonModel.Action>().apply {
            add(Actions.report())
            if (user != null) add(AuthenticationResponses.Actions.logout())
            else {
                add(AuthenticationResponses.Actions.signup())
                add(AuthenticationResponses.Actions.login())
            }
        },
        links = listOf(
            Links.self(Uris.QRCode.makeReport(hash))
        )
    ))
}