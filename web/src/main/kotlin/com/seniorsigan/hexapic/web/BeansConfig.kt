package com.seniorsigan.hexapic.web

import com.seniorsigan.hexapic.FlickrRepository
import com.seniorsigan.hexapic.HexapicService
import com.seniorsigan.hexapic.VkRepository
import com.squareup.okhttp.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BeansConfig {
    @Value("\${flickr_api_key}")
    lateinit var flickrKey: String
    
    @Bean
    open fun okHttpClient() = OkHttpClient()

    @Bean
    open fun jsonParser(): JsonParser = JsonParser()

    @Bean
    open fun hexapicService() = HexapicService(okHttpClient())

    @Bean
    open fun flickrRepository() = FlickrRepository(flickrKey, jsonParser(), okHttpClient())

    @Bean
    open fun vkRepository() = VkRepository(jsonParser(), okHttpClient())
}
