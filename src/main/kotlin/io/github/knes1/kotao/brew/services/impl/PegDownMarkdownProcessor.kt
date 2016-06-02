package io.github.knes1.kotao.brew.services.impl

import io.github.knes1.kotao.brew.services.Processor
import io.github.knes1.kotao.brew.services.Processors
import org.pegdown.PegDownProcessor
import org.springframework.stereotype.Component
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * @author knesek
 * Created on: 5/26/16
 */
@Component("Markdown")
class PegDownMarkdownProcessor : Processor {

    override fun process(input: String, output: OutputStream) {
        val processor = PegDownProcessor()
        val outputStr = processor.markdownToHtml(input)
        BufferedWriter(OutputStreamWriter(output)).use {
            it.write(outputStr)
        }
    }
}