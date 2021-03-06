package io.github.knes1.kotao.brew.services

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface PageEnumerator {

    fun enumeratePages(configuration: Configuration): Sequence<Page>
    fun pageCollections(configuration: Configuration): Map<String, List<Page>>
    fun reset()

}