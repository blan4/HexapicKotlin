package com.seniorsigan.hexapic.web

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.hexapic.IJsonParser

class JsonParser(
    private val objectMapper: ObjectMapper = ObjectMapper()
): IJsonParser {
    init {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    override fun <T> unwrap(json: String, clazz: Class<T>): T {
        return objectMapper.readValue(json, clazz)
    }

    override fun wrap(pojo: Any): String {
        return objectMapper.writeValueAsString(pojo)
    }
}
