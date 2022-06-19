package pt.isel.ps.project.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.project.pipeline.messageconverters.ModifiedJacksonMessageConverter
import pt.isel.ps.project.pipeline.messageconverters.QRreportJsonMessageConverter

@Component
class MvcConfig: WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf{ it is MappingJackson2HttpMessageConverter }

        converters.add(QRreportJsonMessageConverter().apply {
            objectMapper.apply {
                propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
        })

        converters.add(ModifiedJacksonMessageConverter().apply {
            objectMapper.apply {
                propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
            }
        })
    }
}
