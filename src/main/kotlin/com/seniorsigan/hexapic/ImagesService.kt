package com.seniorsigan.hexapic

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

class ImagesService(
    val client: OkHttpClient = OkHttpClient()
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

    fun loadImages(urls: List<String>, count: Int, filter: (BufferedImage) -> Boolean = { true }): List<BufferedImage> {
        if (urls.size < count) throw Exception("Not enough urls to download")
        val executor = Executors.newFixedThreadPool(4)
        val doneSignal = CountDownLatch(count)
        val images = arrayOfNulls<BufferedImage>(count)
        val urlsQueue = ConcurrentLinkedQueue<String>(urls)
        for (i in 0..count - 1) {
            executor.submit({
                while (true) {
                    val url = urlsQueue.poll()
                    if (url == null) {
                        println("Not enough media. It was last")
                    }
                    println("Try to download $url for $i index")
                    try {
                        val image = loadImage(url)
                        if (filter(image)) {
                            images[i] = image
                            println("Downloaded image for $i index")
                            break
                        } else {
                            println("Skipped image $url because of filter")
                            continue
                        }
                    } catch(e: Exception) {
                        println("Error while downloading url $url ${e.message}")
                        continue
                    }
                }
                doneSignal.countDown()
            })
        }
        doneSignal.await(5, TimeUnit.MINUTES)
        executor.shutdown()
        val loadedImages = images.filterNotNull()
        if (loadedImages.size != count) {
            throw Exception("Not enough media. Downloaded only ${loadedImages.size} of $count")
        }
        return loadedImages
    }
}
