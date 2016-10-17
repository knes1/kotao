package io.github.knes1.kotao.brew

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.inject.Guice
import com.mongodb.MongoClient
import io.github.knes1.kotao.brew.services.Generator
import io.github.knes1.kotao.brew.services.impl.DefaultGenerator
import org.apache.log4j.spi.LoggerFactory
import org.jongo.Jongo
import org.jongo.marshall.jackson.JacksonMapper
import org.slf4j.Logger
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Lazy

/**
 * @author knesek
 * Created on: 5/15/16
 */
@SpringBootApplication(
        exclude = arrayOf(RedisAutoConfiguration::class),
        scanBasePackages = arrayOf(
                "io.github.knes1.kotao.brew.services.impl",
                "io.github.knes1.kotao.brew.repositories.impl"
                )
)
open class Application {

    @Bean @Lazy @Conditional()
    open fun mongo(): MongoClient = MongoClient()

    @Bean @Lazy
    open fun jongo(
            mongo: MongoClient,
            properties: MongoProperties,
            beanFactory: AutowireCapableBeanFactory): Jongo {
        val configCustomization = JacksonMapper.Builder()
                .registerModule(KotlinModule())
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
        //We should probably pickup "test" from properites
        //Jongo 1.3, when it comes out, will remove dependency on deprecated methods of
        //Mongo Java Driver
        return Jongo(mongo.getDB("articles"), configCustomization)
    }

}

fun main(args: Array<String>) {

    val log = org.slf4j.LoggerFactory.getLogger(Application::class.java)
    /* //Old spring config
    val context = SpringApplication.run(Application::class.java, *args)
    val generator = context.getBean(Generator::class.java)
    */
    log.info("Kotao started...")
    var time = System.currentTimeMillis()
    val injector = Guice.createInjector(KotaoModule())
    val generator = injector.getInstance(Generator::class.java)
    log.info("Kotao configured in ${System.currentTimeMillis() - time} ms, starting generation...")
    time = System.currentTimeMillis()
    generator.generateAll()
    log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
}
