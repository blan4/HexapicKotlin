package com.seniorsigan.kuestagbot

import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.okhttp.OkHttpClient
import org.apache.ibatis.session.SqlSessionFactory
import org.flywaydb.core.Flyway
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
open class BeansConfig {
    @Bean
    open fun okHttpClient() = OkHttpClient()

    @Bean
    open fun objectMapper() = ObjectMapper()
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