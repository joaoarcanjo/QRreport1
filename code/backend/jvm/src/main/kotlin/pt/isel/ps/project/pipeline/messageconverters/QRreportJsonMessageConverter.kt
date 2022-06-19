package pt.isel.ps.project.pipeline.messageconverters

import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import pt.isel.ps.project.model.representations.QRreportJsonModel

class QRreportJsonMessageConverter: MappingJackson2HttpMessageConverter() {

    override fun canWrite(clazz: Class<*>, mediaType: MediaType?) =
        (mediaType == null || mediaType == QRreportJsonModel.MEDIA_TYPE) && QRreportJsonModel::class.java.isAssignableFrom(clazz)

    override fun canWrite(mediaType: MediaType?) = mediaType == null || mediaType == QRreportJsonModel.MEDIA_TYPE

    override fun getSupportedMediaTypes() = listOf(
        QRreportJsonModel.MEDIA_TYPE,
    )
}
