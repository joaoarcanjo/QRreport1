package pt.isel.ps.project.model.representations

import org.springframework.http.MediaType
import pt.isel.ps.project.exception.InvalidParameter
import java.net.URI

data class ProblemJsonModel(
    val type: URI,
    val title: String,
    val detail: String? = null,
    val instance: String,
    val invalidParams: List<InvalidParameter>? = null,
    val data: Any? = null,
) {
    companion object {
        val MEDIA_TYPE = MediaType.APPLICATION_PROBLEM_JSON
    }
}