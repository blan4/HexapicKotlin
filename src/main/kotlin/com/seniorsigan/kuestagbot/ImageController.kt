package com.seniorsigan.kuestagbot

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import nl.komponents.kovenant.all
import nl.komponents.kovenant.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class ImageController
@Autowired constructor(
        val flickrService: FlickrService,
        val client: OkHttpClient,
        val imagesProcessor: ImagesProcessor
) {
    @RequestMapping(value = "/flickr", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getFromFlickr(
            request: HttpServletRequest,
            response: HttpServletResponse,
            @RequestParam("width", defaultValue = "2") width: Int,
            @RequestParam("height", defaultValue = "2") height: Int,
            @RequestParam("tag", defaultValue = "cat") tag: String
    ) {
        val urls = flickrService.photosByTag(tag).toUrls("z").take(width * height)
        if (urls.size < width * height) {
            response.status = 404
        } else {
            val image = concatenateImages(urls, width, height)
            response.contentType = "image/png"
            ImageIO.write(image, "png", response.outputStream)
        }
    }
    
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
    fun get(request: HttpServletRequest, response: HttpServletResponse) {
        val urls = listOf(
                "https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-15/e35/12331364_1929060240653160_2111305393_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xtp1/t51.2885-15/s640x640/sh0.08/e35/12317447_765483653583679_927037984_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xpf1/t51.2885-15/e35/12357325_139393553093787_1078433963_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/s640x640/sh0.08/e35/12301304_1690088074582988_434684541_n.jpg")
        val image = concatenateImages(urls, 2, 2)
        response.contentType = "image/png"
        ImageIO.write(image, "png", response.outputStream)
    }
    
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