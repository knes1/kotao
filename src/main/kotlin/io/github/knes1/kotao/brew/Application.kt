package io.github.knes1.kotao.brew

import com.google.inject.Guice
import com.google.inject.Injector
import com.sinfulspoonful.kotao.util.lazyLogger
import io.github.knes1.kotao.brew.services.Configuration
import io.github.knes1.kotao.brew.services.Configurator
import io.github.knes1.kotao.brew.services.Generator
import org.slf4j.Logger

/**
 * Kotao entrypoint class. Kotao depends on Guice to inject its main components which are defined in KotaoModule.
 *
 *
 * @author knesek
 * Created on: 5/15/16
 */
class Application {
    //val log: Logger = org.slf4j.LoggerFactory.getLogger(Application::class.java)
    val log: Logger by lazyLogger()
    val injector: Injector = Guice.createInjector(KotaoModule())

    fun init() {
        log.info("Kotao started...")
        var time = System.currentTimeMillis()
        val generator = injector.getInstance(Generator::class.java)
        log.info("Kotao configured in ${System.currentTimeMillis() - time} ms, starting generation...")
        time = System.currentTimeMillis()
        generator.generateAll()
        log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
    }

    fun startServer() {
        val config = injector.getInstance(Configurator::class.java).loadConfiguration()
        WebServer(config.structure.output).deploy()
        log.info("Visit http://localhost:8080/ to browse your site.")
    }
}

fun main(args: Array<String>) {
    val app = Application()
    app.init()
    args.firstOrNull { it == "-s" }?.run { app.startServer() }

}
