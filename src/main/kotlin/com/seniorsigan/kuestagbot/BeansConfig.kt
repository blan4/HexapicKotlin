package com.seniorsigan.kuestagbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.seniorsigan.hexapic.FlickrRepository
import com.seniorsigan.hexapic.HexapicService
import com.seniorsigan.hexapic.InstagramRepository
import com.squareup.okhttp.OkHttpClient
import org.apache.ibatis.session.SqlSessionFactory
import org.flywaydb.core.Flyway
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

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

@Configuration
@MapperScan(basePackages = arrayOf("com.seniorsigan.kuestagbot.mappers"))
open class DatabaseConfig {
    @Autowired
    lateinit public var dataSource: DataSource

    @Bean(initMethod = "migrate")
    open fun flyway(): Flyway {
        val flyway = Flyway()
        flyway.dataSource = dataSource
        return flyway
    }

    @Bean
    open fun sqlSessionFactory(): SqlSessionFactory {
        val bean = SqlSessionFactoryBean()
        bean.setDataSource(dataSource)
        return bean.`object`
    }
}
