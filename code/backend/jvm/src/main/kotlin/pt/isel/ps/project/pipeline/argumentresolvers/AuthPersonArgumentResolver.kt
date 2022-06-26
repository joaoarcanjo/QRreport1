package pt.isel.ps.project.pipeline.argumentresolvers

import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.ps.project.auth.AuthPerson
import pt.isel.ps.project.auth.REQ_ATTRIBUTE_AUTHPERSON

class AuthPersonArgumentResolver: HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType == AuthPerson::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): AuthPerson = webRequest
        .getAttribute(REQ_ATTRIBUTE_AUTHPERSON, RequestAttributes.SCOPE_REQUEST) as AuthPerson
}