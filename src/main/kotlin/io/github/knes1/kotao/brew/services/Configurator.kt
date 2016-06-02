package io.github.knes1.kotao.brew.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.knes1.kotao.brew.services.Configuration

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface Configurator {

    fun loadConfiguration(): Configuration

}