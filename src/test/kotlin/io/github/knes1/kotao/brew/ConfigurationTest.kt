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

    val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    @Test
    fun testLoadConfigFile() {

        val config = ConfigurationTest::class.java.getResourceAsStream("config.yaml").use {
            mapper.readValue(it, Configuration::class.java)
        }

        assertEquals(Configuration(
                listOf(PageCollection(name = "articles"), PageCollection(name = "authors"))
        ), config)
    }


    @Test
    fun testDefaultDeser() {

        val yaml = """
        foo: 4
        kik: "bla"
        """
        val test = mapper.readValue(yaml, TestDefaultDeserialization::class.java)
        assertEquals(TestDefaultDeserialization(foo = 4, kik = "bla"), test)

    }


    data class TestDefaultDeserialization(
            val bar: String = "",
            val foo: Int,
            val kik: String,
            val mao: Int = 4,
            val tib: String? = "tib",
            val xee: Int? = null
    )



}