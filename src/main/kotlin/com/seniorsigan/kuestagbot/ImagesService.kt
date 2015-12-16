package com.seniorsigan.kuestagbot

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
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
    
    fun loadImages(urls: List<String>, count: Int): List<BufferedImage> {
        if (urls.size < count) throw Exception("Not enough urls to download")
        val executor = Executors.newFixedThreadPool(4)
        val doneSignal = CountDownLatch(count)
        val images = arrayOfNulls<BufferedImage>(count)
        val urlsQueue = ConcurrentLinkedQueue<String>(urls)
        for (i in 0..count - 1) {
            executor.submit({
                val url = urlsQueue.poll() ?: throw Exception("Not enough media")
                println("Try to download $url for $i index")
                val image = loadImage(url)
                //some checks
                images[i] = image
                doneSignal.countDown()
                println("Downloaded image for $i index")
            })
        }
        doneSignal.await()
        return images.filterNotNull()
    }

    fun concatenateImages(urls: List<String>, width: Int, height: Int): BufferedImage {
        val images = loadImages(urls, width * height)

        return imagesProcessor.concatenateImages(images, width, height)
    }
}