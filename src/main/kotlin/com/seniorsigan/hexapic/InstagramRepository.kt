package com.seniorsigan.hexapic

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.hexapic.models.InstagramPhotosList
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

class InstagramRepository(
    val clientId: String,
    val objectMapper: ObjectMapper = ObjectMapper(),
    val client: OkHttpClient = OkHttpClient()
) : ImagesRepository {
    val count = 100

    override fun photosUrlsByTag(tag: String): List<String> {
        val media = photosByTag(tag)
        if (media.meta.code == 200) {
            return media.data.filter { it.type.equals("image") }.map { it.images.standard_resolution.url }
        } else {
            return emptyList()
        }
    }

    fun photosByTag(tag: String): InstagramPhotosList {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        val url = "https://api.instagram.com/v1/tags/$tag/media/recent?client_id=$clientId&count=$count"
        println("Send request to $url")
        val req = Request.Builder()
            .url(url)
            .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            println("Instagram response: $data")
            val list = objectMapper.readValue(data, InstagramPhotosList::class.java) ?: InstagramPhotosList()
            println("Instagram give ${list.data.size} photos")
            return list
        } else {
            println("Instagram response with error ${res?.message()}")
            return InstagramPhotosList()
        }
    }
}
