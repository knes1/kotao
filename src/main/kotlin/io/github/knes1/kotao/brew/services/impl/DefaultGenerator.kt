package io.github.knes1.kotao.brew.services.impl

import io.github.knes1.kotao.brew.services.*
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

/**
 * @author knesek
 * Created on: 5/25/16
 */
@Component
class DefaultGenerator @Autowired constructor(
        val configurator: Configurator,
        val enumerator: PageEnumerator,
        val templateEngine: TemplateEngine,
        val processorResolver: ProcessorResolver
) : Generator {

    val conf = configurator.loadConfiguration()
    val paths = conf.structure

    override fun generateAll() {
        val pages = enumerator.enumeratePages(conf)
        pages.forEach {
            generatePage(it, conf)
        }
    }

    private fun processAssets() {
        val assets = File(paths.pathToAssets())
        if (!assets.exists() || !assets.isDirectory) return
        FileUtils.copyDirectory(assets, File(paths.pathToOutput()))
    }

    private fun generatePage(page: Page, configuration: Configuration) {
        processAssets()
        val contentProcessor = Processors.processorFromExtension(page.model["processor"]?.toString()?: page.contentProcessor)
        val processor = processorResolver.resolve(contentProcessor.name)

        val bytesOutput = ByteArrayOutputStream()
        val content = if (processor != null) {
            bytesOutput.use {
                processor.process(page.content, it)
                it.toString("UTF-8")
            }
        } else {
            page.content
        }
        val model = HashMap<String, Any>(configuration.vars)
        model.put("collections", enumerator.pageCollections(configuration))
        model.putAll(page.model)
        model.put("content", content)
        model.put("vars", configuration.vars)
        model.put("page", page)

        val template = model["template"]?.toString()?: page.template

        val path = paths.pathToOutput() + "/" + page.path
        File(path).mkdirs()

        val file = File(path + page.slug + ".html")

        try {
            FileOutputStream(file).use {
                templateEngine.processTemplate(template, model, it)
            }
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Could not create file $path: ${e.message}", e)
        }
    }


}