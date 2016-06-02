package io.github.knes1.kotao.brew

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.knes1.kotao.brew.services.Configuration
import io.github.knes1.kotao.brew.services.PageCollection
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author knesek
 * * Created on: 5/15/16
 */
class ConfigurationTest {

    @Test
    fun testLoadConfigFile() {
        val mapper = ObjectMapper(YAMLFactory())    // Enable YAML parsing
        mapper.registerModule(KotlinModule())       // Enable Kotlin support

        val config = ConfigurationTest::class.java.getResourceAsStream("config.yaml").use {
            mapper.readValue(it, Configuration::class.java)
        }

        assertEquals(Configuration.createWithDefaults(
                listOf(PageCollection.createWithDefaults(name = "articles"), PageCollection.createWithDefaults(name = "authors"))
        ), config)
    }


}