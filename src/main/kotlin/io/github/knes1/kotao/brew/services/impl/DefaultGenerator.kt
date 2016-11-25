package io.github.knes1.kotao.brew.services.impl

import io.github.knes1.kotao.brew.services.*
import io.github.knes1.kotao.brew.util.Url
import io.github.knes1.kotao.brew.util.Utils
import io.github.knes1.kotao.brew.util.toXml
import org.apache.commons.io.FileUtils
import java.io.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.*
import javax.inject.Inject

/**
 * @author knesek
 * Created on: 5/25/16
 */
class DefaultGenerator @Inject constructor(
        val configurator: Configurator,
        val enumerator: PageEnumerator,
        val templateEngine: TemplateEngine,
        val processorResolver: ProcessorResolver
) : Generator {

    val conf = configurator.loadConfiguration()
    val paths = conf.structure

    override fun generateAll() {
        processAssets()
        val pages = enumerator.enumeratePages(conf)
        val siteMap = mutableSetOf<Url>()

        pages.forEach {
            generatePage(it, conf)
            if (conf.site != null && it.sitemap) {
                siteMap.add(Url(conf.site + "/" + it.path + it.slug + ".html", it.publishDate()?: LocalDate.now()))
            }
        }

        if (conf.site != null) {
            siteMap.toXml(FileWriter(paths.output + "/sitemap.xml"))
        }
    }

    private fun processAssets() {
        val assets = File(paths.pathToAssets())
        if (!assets.exists() || !assets.isDirectory) return
        FileUtils.copyDirectory(assets, File(paths.pathToOutput()))
    }

    private fun generatePage(page: Page, configuration: Configuration) {
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
        val collections = enumerator.pageCollections(configuration);
        model.put("collections", collections)
        model.putAll(page.model)
        model.put("content", content)
        model.put("vars", configuration.vars)

        if (configuration.site != null) {
            model.put("site", configuration.site)
        }

        model.put("page", page)

        val template = model["template"]?.toString()?: page.template

        val path = page.directory()
        val pathFile = File(path)
        pathFile.mkdirs()

        val relativeRootDir = Utils.normalizePath(File(paths.pathToOutput()).relativeTo(pathFile).path)

        model.put("rootDir", relativeRootDir)

        //if page's slug doesn't already have an extension, we'll add .html
        val fileExtension = if (page.slug.contains(".")) "" else ".html"

        val file = File(path + page.slug + fileExtension)

        try {
            FileOutputStream(file).use {
                templateEngine.processTemplate(template, model, it)
            }
        } catch (e: FileNotFoundException) {
            throw IllegalStateException("Could not create file $path: ${e.message}", e)
        }
    }

    fun Page.directory() = paths.pathToOutput() + "/" + path

    fun Page.publishDate(): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'[HH:mm[:ss[.SSS[X]]]]]").withResolverStyle(ResolverStyle.LENIENT)
        val dateStr = model["publishDate"]?.toString()
        if (dateStr != null) {
            val result = try { LocalDate.parse(dateStr, formatter) } catch(e: Exception) { null }
            return result
        }
        return null
    }


}