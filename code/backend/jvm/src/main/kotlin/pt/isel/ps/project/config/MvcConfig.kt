package pt.isel.ps.project.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.ps.project.auth.AuthenticationInterceptor
import pt.isel.ps.project.auth.ORIGIN_HEADER
import pt.isel.ps.project.pipeline.argumentresolvers.AuthPersonArgumentResolver
import pt.isel.ps.project.pipeline.interceptors.QueryParamsValidatorInterceptor
import pt.isel.ps.project.pipeline.messageconverters.ModifiedJacksonMessageConverter
import pt.isel.ps.project.pipeline.messageconverters.QRreportJsonMessageConverter

@Component
class MvcConfig(
    private val authInterceptor: AuthenticationInterceptor,
    private val queryParamsInterceptor: QueryParamsValidatorInterceptor
): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
        registry.addInterceptor(queryParamsInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.apply {
            add(AuthPersonArgumentResolver())
        }
    }

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

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowCredentials(true)
            .allowedHeaders(ORIGIN_HEADER)
    }
}
