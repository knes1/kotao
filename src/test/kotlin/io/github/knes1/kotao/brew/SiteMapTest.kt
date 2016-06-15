package io.github.knes1.kotao.brew

import io.github.knes1.kotao.brew.util.Url
import io.github.knes1.kotao.brew.util.toXml
import org.junit.Assert
import org.junit.Test

/**
 * @author knesek
 * Created on: 6/14/16
 */
class SiteMapTest {


    @Test
    fun testSiteMap() {
        val siteMap = setOf(
                Url("http://www.example.com", "2015-01-01"),
                Url("http://www.example.com/kotao", "2016-01-01")
        ).toXml()

        Assert.assertTrue(siteMap.contains("<url><loc>http://www.example.com</loc><lastmod>2015-01-01</lastmod></url>"))
        Assert.assertTrue(siteMap.contains("<url><loc>http://www.example.com/kotao</loc><lastmod>2016-01-01</lastmod></url>"))

    }


}
