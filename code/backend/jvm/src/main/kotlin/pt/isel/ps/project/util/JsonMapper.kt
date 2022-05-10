package pt.isel.ps.project.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val mapper = ObjectMapper().registerKotlinModule()

/*
 * Deserializes a JSON string into a domain object
 */
inline fun <reified T : Any> String.deserializeJsonTo(): T {
    return mapper.readValue(this, T::class.java)
}

/*
 * Serializes domain object to a JSON string
 */
fun Any.serializeToJson(): String {
    return mapper.writeValueAsString(this)
}