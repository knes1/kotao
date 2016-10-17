package io.github.knes1.kotao.brew.services.impl

import com.github.slugify.Slugify
import io.github.knes1.kotao.brew.repositories.AutoCollectionRepository
import io.github.knes1.kotao.brew.repositories.RepositoryResolver
import io.github.knes1.kotao.brew.services.*
import io.github.knes1.kotao.brew.util.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.inject.Inject

/**
 * @author knesek
 * Created on: 5/25/16
 */
@Component
class DefaultPageEnumerator @Inject @Autowired constructor(
        val repositoryResolver: RepositoryResolver
): PageEnumerator {

    private val slugify = Slugify()
    private var loadedCollections: Map<String, List<Page>>? = null


    override fun pageCollections(configuration: Configuration): Map<String, List<Page>> {
        if (loadedCollections != null) {
            return loadedCollections?: throw IllegalStateException()
        }


        //we should probably have separate simple page repository with auto collections
        val simplePages = configuration.pages.flatMap {
            val pathElements = it.name.split("/")
            val slug = pathElements.last()
            val path = Utils.normalizePath(if (pathElements.size > 1) {
                pathElements.subList(0, pathElements.size - 1).joinToString("/")
            } else {
                ""
            })

            val resultList: MutableList<Page> = arrayListOf()

            var firstPageModel: Map<String, Any>? = null

            if (it.paginate != null) {
                //Get repository for referenced collection
                val collectionName = it.paginate.collection
                val collection = configuration.collections.firstOrNull() { it.name == collectionName } ?: throw IllegalArgumentException ("Paginete refrences undefined collection $collectionName")
                val repositoryName = collection.repository
                val repository = if (repositoryName != null) {
                    repositoryResolver.resolve(repositoryName)
                } else {
                    repositoryResolver.defaultRepository()
                }
                val totalElements = repository.count(collectionName)
                val pageSize = it.paginate.pageSize.toLong()
                val totalPages = (totalElements / pageSize) + 1
                for (curPage in 1..totalPages) {
                    val paginator = Paginator(
                        totalElements = totalElements,
                        pageSize = pageSize,
                        currentPage = curPage
                    )

                    //collection holding single page of date from the whole collection
                    val collectionPage = repository.find(collectionName, curPage - 1, pageSize).map { pageDataToPage(it, collection) }.toList()

                    val model: MutableMap<String, Any> =
                            hashMapOf(
                                    "paginator" to paginator,
                                    "collectionPage" to collectionPage)
                    //store model of the first page as we will reuse it on root page
                    if (curPage == 1L) {
                        firstPageModel = model
                    }

                    val page = Page(
                            model = model,
                            template = it.template,
                            path = "page/$curPage/$path",
                            slug = slug,
                            content = "",
                            contentProcessor = "html"
                    )
                    resultList.add(page)
                }

                //add first page to non-paged root directory
                resultList.add(Page(
                        model = firstPageModel?: emptyMap(),
                        template = it.template,
                        path = path,
                        slug = slug,
                        content = "",
                        contentProcessor = "html"
                ))



            } else {
                resultList.add(Page(
                        model = emptyMap<String, Any>(),
                        template = it.template,
                        path = path,
                        slug = slug,
                        content = "",
                        contentProcessor = "html"
                ))
            }
            resultList
        }
        val pageMap = mutableMapOf("simplePages" to simplePages)

        val autoCollections = configuration
                .repositories
                .filter { it.autoCollections }
                .map {
                    repositoryResolver.resolve(it.name)
                }
                .filterIsInstance(AutoCollectionRepository::class.java)
                .flatMap { it.collections() }
        val configCollections = configuration.collections
        val allCollections = autoCollections + configCollections

        allCollections.foldRight(pageMap, {
            pageCollection, map ->
            val repo = if (pageCollection.repository != null) {
                repositoryResolver.resolve(pageCollection.repository)
            } else {
                repositoryResolver.defaultRepository()
            }
            val pages = repo.findAll(pageCollection.name).map { pageDataToPage(it, pageCollection)}.toList()

            map.apply { put(pageCollection.name, pages) }
        })
        loadedCollections = pageMap
        return pageMap
    }


    private fun pageDataToPage(
            pageData: Map<String, Any>,
            pageCollection: PageCollection): Page {

        val slug = slugify.slugify(pageData[pageCollection.slug]?.toString()?: throw IllegalStateException("Could not determine slug for: $pageData"))

        val baseBath = Utils.normalizePath(pageCollection.basePath.trim())
        var path = baseBath + (pageData[pageCollection.pathProperty]?: "").toString()
        path = Utils.normalizePath(path)

        val content = pageData[pageCollection.contentProperty]?.toString()?: throw IllegalStateException("Could not determine content for: $pageData")

        return Page(
                model = pageData,
                template = pageCollection.template,
                path = path,
                slug = slug,
                content = content,
                contentProcessor = pageCollection.contentType
        )
    }


    override fun enumeratePages(configuration: Configuration): Sequence<Page> =
        pageCollections(configuration).values.flatMap { it }.asSequence()


}