package com.seniorsigan.kuestagbot

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import nl.komponents.kovenant.all
import nl.komponents.kovenant.async
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class ImageController {
    val client = OkHttpClient()
    
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
    fun get(request: HttpServletRequest, response: HttpServletResponse) {
        val urls = arrayOf(
                "https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-15/e35/12331364_1929060240653160_2111305393_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xtp1/t51.2885-15/s640x640/sh0.08/e35/12317447_765483653583679_927037984_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xpf1/t51.2885-15/e35/12357325_139393553093787_1078433963_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/s640x640/sh0.08/e35/12301304_1690088074582988_434684541_n.jpg")
        val image = concatenateImages(urls)
        response.contentType = "image/png"
        ImageIO.write(image, "png", response.outputStream)
    }
    
    fun loadImage(url: String): BufferedImage? {
        val req = Request.Builder()
                .url(url)
                .build()
        val res = client.newCall(req).execute()
        if (res != null && res.isSuccessful) {
            return ImageIO.read(res.body()?.byteStream())
        } else {
            println("Request $url was failed ${res.code()} ${res.message()}")
            return null
        }
    }
    
    fun concatenateImages(urls: Array<String>): BufferedImage {
        val promises = urls.map { url ->
            async {
                println("Start loading $url")
                loadImage(url)
            }
        }

        val image = BufferedImage(1280, 1280, BufferedImage.TYPE_INT_RGB)

        all(promises).get().forEachIndexed { i, bufferedImage ->
            val x = (i % 2) * 640
            val y = (i / 2) * 640
            if (bufferedImage != null) {
                image.getSubimage(x, y, 640, 640).data = bufferedImage.data
            }
        }
        
        return image
    }
}