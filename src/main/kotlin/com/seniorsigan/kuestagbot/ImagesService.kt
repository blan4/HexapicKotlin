package com.seniorsigan.kuestagbot

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import nl.komponents.kovenant.all
import nl.komponents.kovenant.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@Service
class ImagesService
@Autowired constructor(
        val client: OkHttpClient, 
        val imagesProcessor: ImagesProcessor
) {
    fun loadImage(url: String): BufferedImage {
        val req = Request.Builder()
                .url(url)
                .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            return ImageIO.read(res.body()?.byteStream())
        } else {
            throw Exception("Request $url was failed ${res.code()} ${res.message()}")
        }
    }

    fun concatenateImages(urls: List<String>, width: Int, height: Int): BufferedImage {
        val promises = urls.map { url ->
            async {
                println("Start loading $url")
                loadImage(url)
            }
        }

        val images = all(promises).get()

        return imagesProcessor.concatenateImages(images, width, height)
    }
}