package io.github.knes1.kotao.brew.services

import java.io.OutputStream

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface TemplateEngine {

    fun processTemplate(templateName: String, model: Map<String, *>, output: OutputStream)

}