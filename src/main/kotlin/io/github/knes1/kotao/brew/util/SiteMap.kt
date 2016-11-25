package io.github.knes1.kotao.brew.util

import java.io.StringWriter
import java.io.Writer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.xml.stream.XMLOutputFactory

/**
 * Utility Url data class and extension functions for generating sitemaps.
 *
 * Sitemap is a set of urls. Therefore, to generate sitemap xml, use toXml extension functions
 * on a set of sitemap urls.
 *
 * Example:
 *
 * setOf(Url("http://example.com", "2016-01-01")).toXml()
 *
 * @author knesek
 * Created on: 6/14/16
 */

data class Url (
    val loc: String,
    val lastmod: String
) {
    constructor(loc: String, lastmod: Instant = Instant.now()) : this(loc, lastmod.atOffset(ZoneOffset.UTC).toLocalDate())
    constructor(loc: String, lastmod: LocalDate) : this(loc, DateTimeFormatter.ISO_DATE.format(lastmod))
}

fun Collection<Url>.toXml(writer: Writer) {
    val factory = XMLOutputFactory.newInstance()
    val sitemapNS = "http://www.sitemaps.org/schemas/sitemap/0.9"
    val schemaNS = "http://www.w3.org/2001/XMLSchema-instance"

    factory.createXMLStreamWriter(writer).apply {
        try {
            writeStartDocument()
            writeStartElement("", "urlset", sitemapNS)
            writeNamespace("", sitemapNS)
            writeNamespace("xsi", schemaNS)
            writeAttribute(schemaNS, "schemaLocation", "$sitemapNS $sitemapNS/sitemap.xsd")
            forEach {
                writeStartElement("url")

                writeStartElement("loc")

                //collapse index.html in sitemap
                val loc = if (it.loc.endsWith("index.html")) {
                    it.loc.substring(0, it.loc.length - "index.html".length)
                } else {
                    it.loc
                }
                writeCharacters(loc)
                writeEndElement()

                writeStartElement("lastmod")
                writeCharacters(it.lastmod)
                writeEndElement()

                writeEndElement() //url
            }

            writeEndElement() //urlset

            writeEndDocument()
        } catch (e: Exception) {
            throw RuntimeException("Failed generating sitemap xml: ${e.message}", e)
        } finally {
            flush()
            close()
        }
    }
}

fun Collection<Url>.toXml() = StringWriter().apply { toXml(this) }.toString()
