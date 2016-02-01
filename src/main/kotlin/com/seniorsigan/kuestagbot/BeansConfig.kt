package com.seniorsigan.kuestagbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.hexapic.FlickrRepository
import com.seniorsigan.hexapic.HexapicService
import com.seniorsigan.hexapic.InstagramRepository
import com.squareup.okhttp.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BeansConfig {
    @Value("\${instagram_client_id}")
    lateinit var instagramKey: String

    @Value("\${flickr_api_key}")
    lateinit var flickrKey: String
    
    @Bean
    open fun okHttpClient() = OkHttpClient()

    @Bean
    open fun objectMapper() = ObjectMapper()
    
    @Bean
    open fun hexapicService() = HexapicService(okHttpClient())
    
    @Bean 
    open fun flickrRepository() = FlickrRepository(flickrKey, objectMapper(), okHttpClient())
    
    @Bean
    open fun instagramRepository() = InstagramRepository(instagramKey, objectMapper(), okHttpClient())
}
