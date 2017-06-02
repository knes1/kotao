package io.github.knes1.kotao.brew

import com.google.inject.Guice
import com.google.inject.Injector
import com.sinfulspoonful.kotao.util.lazyLogger
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.ShowHelpException
import io.github.knes1.kotao.brew.services.Configuration
import io.github.knes1.kotao.brew.services.Configurator
import io.github.knes1.kotao.brew.services.Generator
import io.github.knes1.kotao.brew.util.Utils
import io.github.knes1.kotao.brew.util.getInstance
import io.vertx.core.Vertx
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import java.io.File
import java.nio.charset.Charset

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
        try {
            generator.generateAll()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
        log.info("Page generation finished in ${System.currentTimeMillis() - time} ms.")
    }

    fun watch() {
        val fileWatcher = FileWatcher(config)
        fileWatcher.addChangeListener {
                val time = System.currentTimeMillis()
                vertx.eventBus().send("updates", "starting")
                try {
                    generator.generateAll()
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
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

    fun initNewProject(name: String, overwrite: Boolean) {
        val projDir = File(name)
        if (projDir.exists()) {
            if (projDir.listFiles().isNotEmpty() && !overwrite) {
                throw IllegalArgumentException("Directory $name already exists and is not empty. Please choose a different project name.")
            }
        } else {
            if(!projDir.mkdir()) {
                throw IllegalArgumentException("Error initializing project: could not create directory $name.")
            }
        }

        Utils.copyDirFromResources("/initializers/responsive", projDir, overwrite)

        //Replace variables in config.yaml
        val configFile = File(projDir.path + File.separatorChar + "config.yaml")

        val configFileContents = FileUtils.readFileToString(configFile, Charset.forName("UTF-8"))
                .replace("\${projectName}", name)
                .replace("\${user}", System.getProperty("user.name"))
        FileUtils.write(configFile, configFileContents, Charset.forName("UTF-8"))
        log.info("Initialized new project $name.")
    }
}

fun main(args: Array<String>) {
    val arguments = Arguments(ArgParser(args = args, helpFormatter = DefaultHelpFormatter()))


    val app = Application()
    try {
        arguments.init?.run { app.initNewProject(this, arguments.overwrite); return }
        app.init()
        if (arguments.server) {
            app.startServer()
            app.watch()
        }
    } catch (e: ShowHelpException) {
        e.printAndExit("kotao")
    }
}
