package io.github.knes1.kotao.brew.services.impl

import com.github.slugify.Slugify
import io.github.knes1.kotao.brew.repositories.AutoCollectionRepository
import io.github.knes1.kotao.brew.repositories.RepositoryResolver
import io.github.knes1.kotao.brew.services.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author knesek
 * Created on: 5/25/16
 */
@Component
class DefaultPageEnumerator @Autowired constructor(
        val repositoryResolver: RepositoryResolver
): PageEnumerator {

    private val slugify = Slugify()
    private var loadedCollections: Map<String, List<Page>>? = null


    override fun pageCollections(configuration: Configuration): Map<String, List<Page>> {
        if (loadedCollections != null) {
            return loadedCollections?: throw IllegalStateException()
        }
        //we should probably have separate simple page repository with auto collections
        val simplePages = configuration.pages.map {
            val pathElements = it.name.split("/")
            val slug = pathElements.last()
            val path = normalizePath(if (pathElements.size > 1) {
                pathElements.subList(0, pathElements.size - 1).joinToString("/")
            } else {
                ""
            })
            Page(
                    model = emptyMap<String, Any>(),
                    template = it.template,
                    path = path,
                    slug = slug,
                    content = "",
                    contentProcessor = "html"
            )
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
            val pages = repo.findAll(pageCollection.name).map {
                //TODO: error handling

                val slug = slugify.slugify(it[pageCollection.slug]?.toString()?: throw IllegalStateException("Could not determine slug for: $it"))

                val baseBath = normalizePath(pageCollection.basePath.trim())
                var path = baseBath + (it[pageCollection.pathProperty]?: "").toString()
                path = normalizePath(path)

                val content = it[pageCollection.contentProperty]?.toString()?: throw IllegalStateException("Could not determine content for: $it")

                Page(
                        model = it,
                        template = pageCollection.template,
                        path = path,
                        slug = slug,
                        content = content,
                        contentProcessor = pageCollection.contentType
                )
            }.toList()
            map.apply { put(pageCollection.name, pages) }
        })
        loadedCollections = pageMap
        return pageMap
    }

    private fun normalizePath(path: String) =
        if (path.isNotEmpty() && !path.endsWith("/")) {
            path + "/"
        } else {
            path
        }

    override fun enumeratePages(configuration: Configuration): Sequence<Page> =
        pageCollections(configuration).values.flatMap { it }.asSequence()


}