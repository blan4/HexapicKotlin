package com.seniorsigan.hexapic

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.hexapic.models.VkFeed
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.net.URLEncoder

class VkRepository(
    val objectMapper: ObjectMapper,
    val client: OkHttpClient = OkHttpClient()
): ImagesRepository {
    val count = 200

    override fun photosUrlsByTag(tag: String): List<String> {
        val feed = searchNewsFeed(tag)
        return extractPhotoURLs(feed)
    }

    override fun photosUrlsByUser(username: String): List<String> {
        println("Vk photosUrlsByUser not implemented")
        return emptyList()
    }

    fun extractPhotoURLs(feed: VkFeed): List<String> {
        return feed.response.items.flatMap {
            it.attachments
                .filter { it.type == "photo" }
                .map { it.photo?.photoUrl() }
                .filterNotNull()
        }
    }

    fun searchNewsFeed(tag: String): VkFeed {
        val url = buildURL("newsfeed.search", mapOf("q" to "#$tag", "count" to "$count"))
        println("Send request to $url")
        val req = Request.Builder()
            .url(url)
            .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            val data = res.body().string()
            val feed = objectMapper.readValue(data, VkFeed::class.java) ?: VkFeed()
            return feed
        } else {
            println("VK response with error ${res?.message()}")
            return VkFeed()
        }
    }

    fun buildURL(methodName: String, params: Map<String, String>, version: String = "5.53"): String {
        val p = params.map {
            "${it.key}=${URLEncoder.encode(it.value, "UTF-8")}"
        }.joinToString("&")
        return "https://api.vk.com/method/$methodName?$p&v=$version"
    }
}
