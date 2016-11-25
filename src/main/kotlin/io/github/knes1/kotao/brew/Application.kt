package io.github.knes1.kotao.brew

import com.google.inject.Guice
import io.github.knes1.kotao.brew.services.Generator
import org.slf4j.Logger

/**
 * @author knesek
 * Created on: 5/15/16
 */
class Application {
    val log: Logger = org.slf4j.LoggerFactory.getLogger(Application::class.java)

    fun init() {
        log.info("Kotao started...")
        var time = System.currentTimeMillis()
        val injector = Guice.createInjector(KotaoModule())
        val generator = injector.getInstance(Generator::class.java)
        log.info("Kotao configured in ${System.currentTimeMillis() - time} ms, starting generation...")
        time = System.currentTimeMillis()
        generator.generateAll()
        log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
    }
}

fun main(args: Array<String>) {
    Application().init()
}
