package com.seniorsigan.kuestagbot

import com.seniorsigan.hexapic.FlickrRepository
import com.seniorsigan.hexapic.HexapicService
import com.seniorsigan.hexapic.ImagesRepository
import com.seniorsigan.hexapic.InstagramRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse

@Controller
class ImageController
@Autowired constructor(
    val flickrRepository: FlickrRepository,
    val instagramRepository: InstagramRepository,
    val hexapicService: HexapicService
) {
    @RequestMapping(value = "/tag/{tag}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getTagCollage(
        response: HttpServletResponse,
        @RequestParam("width", defaultValue = "2") width: Int,
        @RequestParam("height", defaultValue = "2") height: Int,
        @RequestParam("source", defaultValue = "") source: String,
        @PathVariable("tag") tag: String
    ) {
        val repositories = when (source) {
            "instagram" -> listOf(instagramRepository)
            "flickr" -> listOf(flickrRepository)
            "all" -> listOf(instagramRepository, flickrRepository)
            else -> listOf(instagramRepository)
        }
        
        buildResponse(hexapicService.loadByTag(repositories, tag, width, height), response)
    }

    @RequestMapping(value = "/user/{user}", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getUserCollage(
        response: HttpServletResponse,
        @RequestParam("width", defaultValue = "2") width: Int,
        @RequestParam("height", defaultValue = "2") height: Int,
        @RequestParam("source", defaultValue = "") source: String,
        @PathVariable("user") username: String
    ) {
        val repositories = when (source) {
            "instagram" -> listOf(instagramRepository)
            "flickr" -> listOf(flickrRepository)
            "all" -> listOf(instagramRepository, flickrRepository)
            else -> listOf(instagramRepository)
        }

        buildResponse(hexapicService.loadByUser(repositories, username, width, height), response)
    }

    fun buildResponse(image: BufferedImage, response: HttpServletResponse) {
        try {
            response.contentType = "image/png"
            ImageIO.write(image, "png", response.outputStream)
        } catch (e: Exception){
            println(e.message)
            response.status = 404
        }
    }
}
