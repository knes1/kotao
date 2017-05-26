package io.github.knes1.kotao.brew.services

import io.github.knes1.kotao.brew.repositories.impl.FileRepositoryConfiguration
import io.github.knes1.kotao.brew.repositories.impl.MongoRepositoryConfiguration
import io.github.knes1.kotao.brew.repositories.impl.RepositoryConfiguration

/**
 * @author knesek
 * Created on: 5/15/16
 */
data class Configuration (
        val collections: List<PageCollection> = emptyList(),
        val pages: List<SimplePage> = emptyList(),
        val vars: Map<String, Any> = emptyMap(),
        val repositories: List<RepositoryConfiguration> = listOf(
                FileRepositoryConfiguration(),
                MongoRepositoryConfiguration()
        ),
        val structure: ProjectStructure = ProjectStructure(),
        val site: String? = null
)

data class ProjectStructure(
    val baseDir: String = "",
    val assets: String = "assets",
    val templates: String = "templates",
    val output: String = "output"
) {
    fun pathToAssets() = pathRelativeToBaseDir(assets)
    fun pathToTemplates() = pathRelativeToBaseDir(templates)
    fun pathToOutput() = pathRelativeToBaseDir(output)

    private fun pathRelativeToBaseDir(relative: String) = if (baseDir.isNullOrBlank()) relative else baseDir + "/" + relative
}


data class PageCollection (
    val name: String = throw InvalidConfigurationException("Configuration section 'collections' - every collection needs to have non empty 'name' parameter."),
    val slug: String = "slug",
    val pathProperty: String? = null,
    val basePath: String = name,
    val contentProperty: String = "content",
    val contentType: String = "html",
    val template: String = name,
    val repository: String? = null
)

data class SimplePage (
    val name: String = throw InvalidConfigurationException("Configuration section 'pages' - every simple page needs to have non empty 'name' parameter."),
    val template: String = name?.split("/")?.last(),
    val paginate: Paginate? = null,
    val sitemap: Boolean = true
)

data class Paginate (
    val collection: String = throw InvalidConfigurationException("Configuration section 'paginate' - every paginate section needs to have a 'collection' parameter that refers to collection name that is going to be paginated."),
    val pageSize: Int = 10
)



