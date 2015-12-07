package com.seniorsigan.kuestagbot

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Controller
class ImageController {
    val client = OkHttpClient()
    
    @RequestMapping(value = "/", method = arrayOf(RequestMethod.GET))
    fun get(request: HttpServletRequest, response: ServletResponse) {
        val urls = arrayOf(
                "https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-15/e35/12331364_1929060240653160_2111305393_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xtp1/t51.2885-15/s640x640/sh0.08/e35/12317447_765483653583679_927037984_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xpf1/t51.2885-15/e35/12357325_139393553093787_1078433963_n.jpg",
                "https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/s640x640/sh0.08/e35/12301304_1690088074582988_434684541_n.jpg")
        val image = BufferedImage(1280, 1280, BufferedImage.TYPE_INT_RGB)
        for ((i, url) in urls.withIndex()) {
            val req = Request.Builder()
                    .url(url)
                    .build()
            val res = client.newCall(req).execute()
            val x = (i % 2) * 640
            val y = (i / 2) * 640
            image.getSubimage(x,y,640, 640).data = ImageIO.read(res.body().byteStream()).data
        }
        
        response.contentType = "image/png"
        ImageIO.write(image, "png", response.outputStream)
    }
}