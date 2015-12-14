package com.seniorsigan.kuestagbot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.kuestagbot.models.InstagramPhotosList
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InstagramRepository
@Autowired constructor(
    val objectMapper: ObjectMapper,
    val client: OkHttpClient
): ImagesRepository {
    val CLIENT_ID = "417c3ee8c9544530b83aa1c24de2abb3"
    
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
        val url = "https://api.instagram.com/v1/tags/$tag/media/recent?client_id=$CLIENT_ID&count=100"
        println("Send request to $url")
        val req = Request.Builder()
                .url(url)
                .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            println("Instagram response: $data")
            return objectMapper.readValue(data, InstagramPhotosList::class.java) ?: InstagramPhotosList()
        } else {
            println("Instagram response with error ${res?.message()}")
            return InstagramPhotosList()
        }
    } 
}