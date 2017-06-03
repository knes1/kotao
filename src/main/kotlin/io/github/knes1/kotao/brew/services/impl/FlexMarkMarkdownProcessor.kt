package io.github.knes1.kotao.brew.services.impl

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import io.github.knes1.kotao.brew.services.Processor
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * @author knesek
 * Created on: 6/3/17
 */
class FlexMarkMarkdownProcessor : Processor {


	override fun process(input: String, output: OutputStream) {
		val options = MutableDataSet()

		// uncomment to set optional extensions
		//options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

		// uncomment to convert soft-breaks to hard breaks
		//options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

		val parser = Parser.builder(options).build()
		val renderer = HtmlRenderer.builder(options).build()

		// You can re-use parser and renderer instances
		val document = parser.parse(input)
		val result = renderer.render(document)
		BufferedWriter(OutputStreamWriter(output)).use {
			it.write(result)
		}

	}
}