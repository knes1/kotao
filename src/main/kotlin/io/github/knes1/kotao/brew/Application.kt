package io.github.knes1.kotao.brew

import com.google.inject.Guice
import com.google.inject.Injector
import com.sinfulspoonful.kotao.util.lazyLogger
import io.github.knes1.kotao.brew.services.Configuration
import io.github.knes1.kotao.brew.services.Configurator
import io.github.knes1.kotao.brew.services.Generator
import io.github.knes1.kotao.brew.util.getInstance
import io.vertx.core.Vertx
import org.slf4j.Logger

/**
 * Kotao entry point class. Kotao depends on Guice to inject its main components which are defined in KotaoModule.
 *
 * @author knesek
 * Created on: 5/15/16
 */
class Application {
    //val log: Logger = org.slf4j.LoggerFactory.getLogger(Application::class.java)
    val log: Logger by lazyLogger()
    val injector: Injector by lazy { Guice.createInjector(KotaoModule()) }
    val generator: Generator by lazy { injector.getInstance(Generator::class) }
    val config: Configuration by lazy { injector.getInstance(Configurator::class.java).loadConfiguration() }
    val vertx by lazy { Vertx.vertx() }

    fun init() {
        log.info("Kotao started...")
        var time = System.currentTimeMillis()
        val generator = this.generator
        log.info("Kotao configured in ${System.currentTimeMillis() - time} ms, starting generation...")
        time = System.currentTimeMillis()
        generator.generateAll()
        log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
    }

    fun watch() {
        val fileWatcher = FileWatcher(config)
        fileWatcher.addChangeListener {
            val time = System.currentTimeMillis()
            vertx.eventBus().send("updates", "starting")
            generator.generateAll()
            vertx.eventBus().send("updates", "finished")
            log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
        }
        fileWatcher.watch()
    }

    fun startServer() {
        vertx.deployVerticle(WebServer(config.structure.output)) {
            if (it.succeeded()) {
                log.info("Visit http://localhost:8080/ to browse your site.")
            }
        }
    }
}

fun main(args: Array<String>) {
    val app = Application()
    app.init()
    args.firstOrNull { it == "-s" }?.run {
        app.startServer()
        app.watch()
    }

}
