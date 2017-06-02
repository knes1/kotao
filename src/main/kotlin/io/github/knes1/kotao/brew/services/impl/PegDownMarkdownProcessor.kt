package io.github.knes1.kotao.brew.services.impl

import io.github.knes1.kotao.brew.services.Processor
import org.pegdown.Extensions
import org.pegdown.PegDownProcessor
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * @author knesek
 * Created on: 5/26/16
 */
class PegDownMarkdownProcessor : Processor {

    override fun process(input: String, output: OutputStream) {
        //TODO make extensions confgurable
        //val processor = PegDownProcessor(Extensions.ANCHORLINKS)
        val processor = PegDownProcessor()
        val outputStr = processor.markdownToHtml(input)
        BufferedWriter(OutputStreamWriter(output)).use {
            it.write(outputStr)
        }
    }
}