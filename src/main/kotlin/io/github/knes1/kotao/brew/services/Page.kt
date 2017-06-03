package io.github.knes1.kotao.brew.services

/**
 * @author knesek
 * Created on: 5/25/16
 */
data class Page(
        val model: Map<String, Any>,
        val template: String,
        val path: String,
        val slug: String,
        val content: String,
        val contentProcessor: String,
        val sitemap: Boolean
) {
	/**
	 * Link from the root of the site to the page.
	 */
	val link: String
		get() = path + slug + "." + contentProcessor
}
