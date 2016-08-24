package com.seniorsigan.hexapic

import com.seniorsigan.hexapic.models.FlickrPhotosList
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

class FlickrRepository(
    val apiKey: String,
    val parser: IJsonParser,
    val client: OkHttpClient = OkHttpClient()
) : ImagesRepository {
    val count = 100

    override fun photosUrlsByTag(tag: String): List<String> {
        val photos = photosByTag(tag)
        return photos.toUrls("z")
    }

    fun photosByTag(tag: String): FlickrPhotosList {
        val method = "flickr.photos.search"
        val url = "https://api.flickr.com/services/rest/?method=$method&api_key=$apiKey&tags=$tag&per_page=$count&format=json&nojsoncallback=1"
        println("Send request to $url")
        val req = Request.Builder()
            .url(url)
            .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            val list = parser.unwrap(data, FlickrPhotosList::class.java) ?: FlickrPhotosList()
            return list
        } else {
            println("Flickr response with error ${res?.message()}")
            return FlickrPhotosList()
        }
    }
}
