package com.seniorsigan.hexapic

interface IJsonParser {
    fun wrap(pojo: Any): String
    fun <T> unwrap(json: String, clazz: Class<T>): T?
}
