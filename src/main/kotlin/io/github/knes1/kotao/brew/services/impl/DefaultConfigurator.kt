package io.github.knes1.kotao.brew.services.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.knes1.kotao.brew.services.Configuration
import io.github.knes1.kotao.brew.services.Configurator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Named

/**
 * @author knesek
 * Created on: 5/26/16
 */
@Component
class DefaultConfigurator @Inject constructor(@Value("\${config.path:}") @Named("config.path") val pathToConfig: String?) : Configurator {
    val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    override fun loadConfiguration(): Configuration {
        val configFile = File(if (pathToConfig.isNullOrBlank()) "config.yaml" else pathToConfig?.trim() + "/config.yaml")
        val basePath = configFile.canonicalFile.parentFile

        var config = FileInputStream(configFile).use {
            mapper.readValue(it, Configuration::class.java)
        }

        val configBasePath = File(config.structure.baseDir)

        if (!configBasePath.isAbsolute && configBasePath.canonicalFile != basePath) {
           config = config.copy(structure = config.structure.copy(baseDir = basePath.toString() + "/" + configBasePath))
        }

        return config
    }



}