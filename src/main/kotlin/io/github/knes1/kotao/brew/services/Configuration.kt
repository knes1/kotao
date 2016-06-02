package io.github.knes1.kotao.brew.services

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.knes1.kotao.brew.repositories.impl.FileRepositoryConfiguration
import io.github.knes1.kotao.brew.repositories.impl.MongoRepositoryConfiguration
import io.github.knes1.kotao.brew.repositories.impl.RepositoryConfiguration

/**
 * @author knesek
 * Created on: 5/15/16
 */
data class Configuration private constructor(
        val collections: List<PageCollection>,
        val pages: List<SimplePage>,
        val vars: Map<String, Any>,
        val repositories: List<RepositoryConfiguration>,
        val structure: ProjectStructure
) {
    companion object {
        @JvmStatic @JsonCreator
        fun createWithDefaults(
            collections: List<PageCollection>? = emptyList(),
            pages: List<SimplePage>? = emptyList(),
            vars: Map<String, Any>? = emptyMap(),
            repositories: List<RepositoryConfiguration>? = defaultRepositories,
            structure: ProjectStructure? = defaultStructure

        ) = Configuration(
                collections = collections?: emptyList(),
                pages = pages?: emptyList(),
                vars = vars?: emptyMap(),
                repositories = repositories?: defaultRepositories,
                structure = structure?: defaultStructure
            )


        val defaultRepositories = listOf(
                FileRepositoryConfiguration.createWithDefaults(),
                MongoRepositoryConfiguration.createWithDefaults()
        )

        val defaultStructure = ProjectStructure.createWithDefaults()
    }
}

data class ProjectStructure private constructor(
    val baseDir: String,
    val assets: String,
    val templates: String,
    val output: String
) {

    fun pathToAssets() = pathRelativeToBaseDir(assets)
    fun pathToTemplates() = pathRelativeToBaseDir(templates)
    fun pathToOutput() = pathRelativeToBaseDir(output)

    private fun pathRelativeToBaseDir(relative: String) = if (baseDir.isNullOrBlank()) relative else baseDir + "/" + relative

    companion object {
        @JvmStatic @JsonCreator
        fun createWithDefaults(
            baseDir: String? = "",
            assets: String? = "assets",
            templates: String? = "templates",
            output: String? = "output"
        ) = ProjectStructure(
                baseDir = baseDir?: "",
                assets = assets?: "assets",
                templates = templates?: "templates",
                output = output?: "output"
        )
    }
}


data class PageCollection private constructor(
    val name: String,
    val slug: String,
    val pathProperty: String?,
    val basePath: String,
    val contentProperty: String,
    val contentType: String,
    val template: String,
    val repository: String?
) {
    companion object {
        @JvmStatic @JsonCreator
        fun createWithDefaults(
                name: String?,
                slug: String? = "slug",
                pathProperty: String? = null,
                basePath: String? = name,
                contentProperty: String? = "content",
                contentType: String? = "html",
                template: String? = name,
                repository: String? = null
        ) = PageCollection(
                name ?: throw IllegalArgumentException(),
                slug ?: "slug",
                pathProperty,
                basePath ?: name,
                contentProperty ?: "content",
                contentType ?: "html",
                template ?: name,
                repository
        )
    }

}

data class SimplePage private constructor(
    val name: String,
    val template: String
) {
    companion object {
        @JvmStatic @JsonCreator
        fun createWithDefaults(
                name: String?,
                template: String? = name?.split("/")?.last()
        ) = SimplePage(
                name ?: throw IllegalArgumentException(),
                template ?: name.split("/").last()
        )
    }
}
