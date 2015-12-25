package com.seniorsigan.kuestagbot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class ImageController
@Autowired constructor(
    val flickrRepository: FlickrRepository,
    val instagramRepository: InstagramRepository,
    val imagesService: ImagesService
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
        val urls = flickrRepository.photosUrlsByTag(tag)
        buildResponse(urls, width, height, response)
    }

    @RequestMapping(value = "/instagram", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getFromInstagram(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestParam("width", defaultValue = "2") width: Int,
        @RequestParam("height", defaultValue = "2") height: Int,
        @RequestParam("tag", defaultValue = "cat") tag: String
    ) {
        val urls = instagramRepository.photosUrlsByTag(tag)
        buildResponse(urls, width, height, response)
    }

    fun buildResponse(urls: List<String>, width: Int, height: Int, response: HttpServletResponse) {
        Collections.shuffle(urls)
        if (urls.size < width * height) {
            response.status = 404
        } else {
            val image = imagesService.concatenateImages(urls, width, height)
            response.contentType = "image/png"
            ImageIO.write(image, "png", response.outputStream)
        }
    }
}
