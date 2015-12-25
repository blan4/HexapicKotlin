package com.seniorsigan.kuestagbot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.kuestagbot.models.FlickrPhotosList
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FlickrRepository
@Autowired constructor(
    val objectMapper: ObjectMapper,
    val client: OkHttpClient
) : ImagesRepository {

    @Value("\${flickr_api_key}")
    lateinit var API_KEY: String

    override fun photosUrlsByTag(tag: String): List<String> {
        val photos = photosByTag(tag)
        return photos.toUrls("z")
    }

    fun photosByTag(tag: String): FlickrPhotosList {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        val method = "flickr.photos.search"
        val url = "https://api.flickr.com/services/rest/?method=$method&api_key=$API_KEY&tags=$tag&per_page=100&format=json&nojsoncallback=1"
        println("Send request to $url")
        val req = Request.Builder()
            .url(url)
            .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            println("Flickr response: $data")
            return objectMapper.readValue(data, FlickrPhotosList::class.java) ?: FlickrPhotosList()
        } else {
            println("Flickr response with error ${res?.message()}")
            return FlickrPhotosList()
        }
    }
}

fun FlickrPhotosList.toUrls(size: String) = photos.photo.map { it.url(size) }
