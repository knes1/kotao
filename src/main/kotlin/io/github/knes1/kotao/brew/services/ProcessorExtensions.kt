package io.github.knes1.kotao.brew.services

/**
 * @author knesek
 * Created on: 5/28/16
 */
enum class Processors private constructor(vararg val extension: String) {
    Markdown("md", "markdown"),
    None("html", "none", "plain", "txt");

    companion object {
        fun processorFromExtension(extension: String) =
            values().filter { it.extension.contains(extension) }.firstOrNull()?: None
    }
}