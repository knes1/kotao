package io.github.knes1.kotao.brew

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

/**
 * @author knesek
 * Created on: 6/1/17
 */
class Arguments(parser: ArgParser) {
	val server by parser.flagging(names = "-s", help = "Start development web server and watch filesystem for changes").default(false)
	val init: String? by parser.storing(names = "--init", help = "Initializes a new Kotao static site project in a new directory.").default(null)
	val overwrite by parser.flagging(names = "--overwrite", help = "Used together with --init, forces overwriting any existing files when initializing new project").default(false)
}