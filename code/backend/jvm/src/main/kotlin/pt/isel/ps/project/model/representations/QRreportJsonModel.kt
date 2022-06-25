package pt.isel.ps.project.model.representations

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

class QRreportJsonModel(
    @JsonProperty("class")
    val clazz: List<String>,
    val rel: List<String>? = null,
    val properties: Any? = null,
    val entities: List<QRreportJsonModel>? = null,
    val actions: List<Action>? = null,
    val links: List<Link>,
) {

    data class PropertyValue(
        val href: String? = null,
        val values: Any? = null,
    )

    class Property(
        val name: String,
        val type: String,
        val itemsType: String? = null,
        val required: Boolean? = null,
        val possibleValues: PropertyValue? = null,
    )
    class Action(
        val name: String,
        val title: String,
        val method: HttpMethod,
        val href: String,
        val type: String? = null,
        val properties: List<Property>? = null,
    )
    class Link(
        val rel: List<String>,
        val href: String,
        val templated: Boolean? = null,
    )
    companion object {
        val MEDIA_TYPE = MediaType("application", "vnd.qrreport+json")
    }
}