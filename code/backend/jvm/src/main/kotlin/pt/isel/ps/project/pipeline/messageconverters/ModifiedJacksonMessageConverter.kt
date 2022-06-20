package pt.isel.ps.project.pipeline.messageconverters

import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import pt.isel.ps.project.model.representations.ProblemJsonModel
import pt.isel.ps.project.model.representations.QRreportJsonModel
import java.lang.reflect.Type

class ModifiedJacksonMessageConverter: MappingJackson2HttpMessageConverter() {

    override fun canWrite(clazz: Class<*>, mediaType: MediaType?) =
        !QRreportJsonModel::class.java.isAssignableFrom(clazz) && super.canWrite(clazz, mediaType)

    override fun canWrite(type: Type?, clazz: Class<*>, mediaType: MediaType?) =
        !QRreportJsonModel::class.java.isAssignableFrom(clazz) && super.canWrite(type, clazz, mediaType)

    override fun getSupportedMediaTypes() = listOf(
        MediaType("application","json"),
        ProblemJsonModel.MEDIA_TYPE,
    )
}
