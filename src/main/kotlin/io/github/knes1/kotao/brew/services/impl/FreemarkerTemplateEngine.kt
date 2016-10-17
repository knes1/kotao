package io.github.knes1.kotao.brew.services.impl

import freemarker.template.TemplateExceptionHandler
import io.github.knes1.kotao.brew.services.Configurator
import freemarker.template.Configuration as FreeMarkerConfig
import io.github.knes1.kotao.brew.services.TemplateEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject

/**
 * @author knesek
 * Created on: 5/26/16
 */
@Component
class FreemarkerTemplateEngine @Inject @Autowired constructor(configurator: Configurator): TemplateEngine {

    val freemarkerConfiguration: FreeMarkerConfig

    init {
        val templatePath = configurator.loadConfiguration().structure.pathToTemplates()
        freemarkerConfiguration = FreeMarkerConfig(FreeMarkerConfig.VERSION_2_3_24).apply {
            setDirectoryForTemplateLoading(File(templatePath))
            defaultEncoding = "UTF-8"
            templateExceptionHandler = TemplateExceptionHandler.HTML_DEBUG_HANDLER
            logTemplateExceptions = false
        }
    }

    override fun processTemplate(templateName: String, model: Map<String, *>, output: OutputStream) {
        val template = freemarkerConfiguration.getTemplate(templateName + ".ftlh")
        template.process(model, OutputStreamWriter(output))
    }

}