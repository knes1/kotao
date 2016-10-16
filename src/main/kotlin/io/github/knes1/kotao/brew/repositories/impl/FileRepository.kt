package io.github.knes1.kotao.brew.repositories.impl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.knes1.kotao.brew.repositories.AutoCollectionRepository
import io.github.knes1.kotao.brew.services.Configurator
import io.github.knes1.kotao.brew.services.PageCollection
import io.github.knes1.kotao.brew.services.Processors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.util.*

/**
 * @author knesek
 * Created on: 5/27/16
 */
@Component
class FileRepository @Autowired constructor(val configurator: Configurator) : AutoCollectionRepository {

    companion object {
        val ROOT_COLLECTION_NAME = "_root"
    }

    override fun name(): String = "file"

    override fun collections(): List<PageCollection> {
        val root = File(config.basePath)
        val allDirs = getSubDirectoriesRecursively(root) + root

        val children = allDirs.map {
            val relativeName = it.toRelativeString(root)
            val name = if (!relativeName.isEmpty()) relativeName else ROOT_COLLECTION_NAME
            val basePath = if (!relativeName.isEmpty()) name else ""
            PageCollection.createWithDefaults(
                name = name,
                repository = "file",
                contentProperty = "content",
                slug = "slug",
                basePath = basePath
            )
        }
        return children
    }

    val config: FileRepositoryConfiguration
    val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    init {
        val configuration = configurator.loadConfiguration()
        config = configuration.repositories
                .filterIsInstance(FileRepositoryConfiguration::class.java)
                .firstOrNull()?: FileRepositoryConfiguration.createWithDefaults()
    }

    override fun findAll(name: String): Sequence<Map<String, Any>> {
        //Validation
        val collectionName = if (name == ROOT_COLLECTION_NAME) "" else name

        val root = File(config.basePath)
        if (collectionName.startsWith("/")) throw IllegalArgumentException("Collection names in file repository may not start with /")
        if (!root.exists() && !root.isDirectory) throw IllegalArgumentException("File repository's content root $root is not a directory.")
        val collectionDir = File(root.absolutePath + File.separator + collectionName).absoluteFile
        if (!isFileChildOf(root, collectionDir)) throw IllegalArgumentException("Collections of file repository need to be inside content root $root.")
        if (!collectionDir.exists() || !collectionDir.isDirectory) throw  IllegalArgumentException("Collection names need to be directories inside $root, $collectionDir is not a directory or does not exist")

        val result = collectionDir
                .listFiles()
                .asSequence()
                .filterNot { it.isDirectory }
                .map {
                    parseFile(it)
                }
        return result
    }

    override fun count(name: String): Long {
        throw UnsupportedOperationException("pagination support not implemented")
    }

    override fun find(name: String, pageStart: Long, pageSize: Long): Sequence<Map<String, Any>> {
        throw UnsupportedOperationException("pagination support not implemented")
    }


    fun parseFile(contentFile: File): Map<String, Any> {
        val fileContents = contentFile.readText(Charsets.UTF_8).trim()
        val frontMatterIndex = if (fileContents.startsWith("---\n")) {
            fileContents.indexOf("---\n", 4)
        } else {
            fileContents.indexOf("---\n", 0)
        }
        val model = if (frontMatterIndex > 0) {
            val frontMatter = fileContents.substring(0, frontMatterIndex)
            val mapType: TypeReference<HashMap<String, Any>> = object : TypeReference<HashMap<String, Any>>() {}
            mapper.readValue<HashMap<String, Any>>(frontMatter, mapType)
        } else {
            HashMap()
        }
        val content = if (frontMatterIndex > 0) {
            fileContents.substring(frontMatterIndex + 4, fileContents.length)
        } else {
            fileContents
        }
        model.put("content", content)
        model.putIfAbsent("filename", contentFile.name.split(File.separator).last())
        model.putIfAbsent("slug", contentFile.nameWithoutExtension.split(File.separator).last())
        model.putIfAbsent("extension", contentFile.extension)
        model.putIfAbsent("processor", contentFile.extension)
        return model
    }

    fun getSubDirectoriesRecursively(base: File): List<File> {
        if (!base.exists() || !base.isDirectory) return emptyList()
        val dirs = base.listFiles().filter { it.isDirectory }.flatMap { getSubDirectoriesRecursively(it) + it }
        return dirs
    }

    fun isFileChildOf(parent: File, child: File) =
            child.canonicalPath == parent.canonicalPath ||
            child.canonicalPath.startsWith(parent.canonicalPath + File.separator);
}

class FileRepositoryConfiguration private constructor(
        override val name: String,
        override val autoCollections: Boolean,
        val basePath: String? = "content"
) : RepositoryConfiguration() {
    companion object {
        @JvmStatic @JsonCreator
        fun createWithDefaults(
                name: String? = "file",
                autoCollections: Boolean? = true,
                basePath: String? = "content"
        ) = FileRepositoryConfiguration(
                name = name?: "file",
                autoCollections = autoCollections?: true,
                basePath = basePath?: "content"
        )
    }
}