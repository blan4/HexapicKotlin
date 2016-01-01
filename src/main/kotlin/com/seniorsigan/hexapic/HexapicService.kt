package com.seniorsigan.hexapic

import com.squareup.okhttp.OkHttpClient
import java.awt.image.BufferedImage
import java.util.*

class HexapicService(
    val httpClient: OkHttpClient = OkHttpClient(),
    val imagesProcessor: ImagesProcessor = ImagesProcessor(),
    val imagesService: ImagesService = ImagesService(httpClient)
) {
    fun loadByTag(repository: ImagesRepository, tag: String, width: Int = 3, height: Int = 2): BufferedImage {
        val urls = repository.photosUrlsByTag(tag)
        if (urls.size < width * height) throw Exception("Not enough media")
        Collections.shuffle(urls)
        val images = imagesService.loadImages(urls, width * height)
        return imagesProcessor.concatenateImages(images, width, height)
    }
    
    fun loadByTag(repositories: List<ImagesRepository>, tag: String, width: Int = 3, height: Int = 2): BufferedImage {
        val urls = loadUrlsByTag(repositories, tag)
        if (urls.size < width * height) throw Exception("Not enough media")
        Collections.shuffle(urls)
        val images = imagesService.loadImages(urls, width * height)
        return imagesProcessor.concatenateImages(images, width, height)
    }
    
    private fun loadUrlsByTag(repositories: List<ImagesRepository>, tag: String): List<String> {
        return repositories.map { 
            it.photosUrlsByTag(tag)
        }.flatten()
    }
}
