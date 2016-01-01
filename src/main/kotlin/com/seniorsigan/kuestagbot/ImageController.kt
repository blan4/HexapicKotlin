package com.seniorsigan.kuestagbot

import com.seniorsigan.hexapic.FlickrRepository
import com.seniorsigan.hexapic.HexapicService
import com.seniorsigan.hexapic.ImagesRepository
import com.seniorsigan.hexapic.InstagramRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse

@Controller
class ImageController
@Autowired constructor(
    val flickrRepository: FlickrRepository,
    val instagramRepository: InstagramRepository,
    val hexapicService: HexapicService
) {
    @RequestMapping(value = "/flickr", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getFromFlickr(
        response: HttpServletResponse,
        @RequestParam("width", defaultValue = "2") width: Int,
        @RequestParam("height", defaultValue = "2") height: Int,
        @RequestParam("tag", defaultValue = "cat") tag: String
    ) {
        buildResponse(flickrRepository, tag, width, height, response)
    }

    @RequestMapping(value = "/instagram", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getFromInstagram(
        response: HttpServletResponse,
        @RequestParam("width", defaultValue = "2") width: Int,
        @RequestParam("height", defaultValue = "2") height: Int,
        @RequestParam("tag", defaultValue = "cat") tag: String
    ) {
        buildResponse(instagramRepository, tag, width, height, response)
    }

    @RequestMapping(value = "/all", method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getFromAll(
        response: HttpServletResponse,
        @RequestParam("width", defaultValue = "2") width: Int,
        @RequestParam("height", defaultValue = "2") height: Int,
        @RequestParam("tag", defaultValue = "cat") tag: String
    ) {
        buildResponse(listOf<ImagesRepository>(instagramRepository, flickrRepository), tag, width, height, response)
    }

    fun buildResponse(repository: ImagesRepository, tag: String, width: Int, height: Int, response: HttpServletResponse) {
        try {
            val image = hexapicService.loadByTag(repository, tag, width, height)
            response.contentType = "image/png"
            ImageIO.write(image, "png", response.outputStream)
        } catch (e: Exception){
            println(e.message)
            response.status = 404
        }
    }

    fun buildResponse(repositories: List<ImagesRepository>, tag: String, width: Int, height: Int, response: HttpServletResponse) {
        try {
            val image = hexapicService.loadByTag(repositories, tag, width, height)
            response.contentType = "image/png"
            ImageIO.write(image, "png", response.outputStream)
        } catch (e: Exception){
            println(e.message)
            response.status = 404
        }
    }
}
