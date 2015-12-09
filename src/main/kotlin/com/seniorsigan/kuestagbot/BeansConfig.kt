package com.seniorsigan.kuestagbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.okhttp.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class BeansConfig {
    @Bean
    open fun okHttpClient() = OkHttpClient()

    @Bean
    open fun objectMapper() = ObjectMapper()
}