package io.github.knes1.kotao.brew

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.inject.AbstractModule
import com.google.inject.Provider
import com.google.inject.Provides
import com.google.inject.name.Names
import com.mongodb.MongoClient
import io.github.knes1.kotao.brew.repositories.RepositoryResolver
import io.github.knes1.kotao.brew.repositories.impl.FileRepository
import io.github.knes1.kotao.brew.repositories.impl.MongoRepository
import io.github.knes1.kotao.brew.services.*
import io.github.knes1.kotao.brew.services.impl.*
import org.jongo.Jongo
import org.jongo.marshall.jackson.JacksonMapper

/**
 * @author knesek
 * Created on: 10/17/16
 */

class KotaoModule : AbstractModule() {

    override fun configure() {
        bind(Configurator::class.java).to(DefaultConfigurator::class.java)
        bindConstant().annotatedWith(Names.named("config.path")).to("")

        bind(Generator::class.java).to(DefaultGenerator::class.java)

        bind(PageEnumerator::class.java).to(DefaultPageEnumerator::class.java)

        bind(TemplateEngine::class.java).to(FreemarkerTemplateEngine::class.java)

        bind(ProcessorResolver::class.java).toProvider(ProcessorResolverProvider::class.java)
    }

    @Provides
    fun repositoryResolver(configurator: Configurator, jongo: Jongo): RepositoryResolver {
        return DefaultRepositoryResolver(listOf(
                FileRepository(configurator),
                MongoRepository(jongo)
                ))
    }

    @Provides
    fun jongo(): Jongo {
        val configCustomization = JacksonMapper.Builder()
                .registerModule(KotlinModule())
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
        //We should probably pickup "test" from properites
        //Jongo 1.3, when it comes out, will remove dependency on deprecated methods of
        //Mongo Java Driver
        val mongo = MongoClient()
        return Jongo(mongo.getDB("articles"), configCustomization)
    }

    class ProcessorResolverProvider : Provider<ProcessorResolver> {
        override fun get(): ProcessorResolver {
            return DefaultProcessorResolver(mapOf("Markdown" to FlexMarkMarkdownProcessor()))
        }
    }

}