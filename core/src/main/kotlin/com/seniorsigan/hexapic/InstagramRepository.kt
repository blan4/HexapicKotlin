package com.seniorsigan.hexapic

import com.seniorsigan.hexapic.models.InstagramPhotosList
import com.seniorsigan.hexapic.models.InstagramUserSearch
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

@Deprecated("Read new Instagram service policy")
class InstagramRepository(
    val clientId: String,
    val parser: IJsonParser,
    val client: OkHttpClient = OkHttpClient()
) : ImagesRepository {
    val count = 100

    override fun photosUrlsByTag(tag: String): List<String> {
        val url = "https://api.instagram.com/v1/tags/$tag/media/recent?client_id=$clientId&count=$count"
        
        return selectImageUrls(getMedia(url))
    }

    fun photosUrlsByUser(username: String): List<String> {
        val userId = findUserId(username) ?: return emptyList()
        val url = "https://api.instagram.com/v1/users/$userId/media/recent?client_id=$clientId&count=$count"

        return selectImageUrls(getMedia(url))
    }
    
    fun selectImageUrls(media: InstagramPhotosList): List<String> {
        if (media.meta.code == 200) {
            return media.data.filter { it.type.equals("image") }.map { it.images.standard_resolution.url }
        } else {
            return emptyList()
        }
    }

    fun getMedia(url: String): InstagramPhotosList {
        println("Send request to $url")
        val req = Request.Builder()
            .url(url)
            .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            println("Instagram response: $data")
            val list = parser.unwrap(data, InstagramPhotosList::class.java) ?: InstagramPhotosList()
            println("Instagram give ${list.data.size} photos")
            return list
        } else {
            println("Instagram response with error ${res?.message()}")
            return InstagramPhotosList()
        }
    }

    fun findUserId(username: String): String? {
        val url = "https://api.instagram.com/v1/users/search/?q=$username&client_id=$clientId"
        println("Send request to $url")
        val req = Request.Builder()
            .url(url)
            .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            println("Instagram response: $data")
            val list = parser.unwrap(data, InstagramUserSearch::class.java) ?: InstagramUserSearch()
            println("Instagram give ${list.data.size} users")
            return list.data.find { it.username == username }?.id
        } else {
            println("Instagram response with error ${res?.message()}")
            return null
        }
    }
}
