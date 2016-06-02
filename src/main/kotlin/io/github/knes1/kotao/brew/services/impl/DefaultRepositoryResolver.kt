package io.github.knes1.kotao.brew.services.impl

import io.github.knes1.kotao.brew.repositories.Repository
import io.github.knes1.kotao.brew.repositories.RepositoryResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author knesek
 * Created on: 5/26/16
 */
@Component
class DefaultRepositoryResolver @Autowired constructor(
       repositories: List<Repository>
) : RepositoryResolver {

    val repositoryResolutionMap: Map<String, Repository> = mapOf(
            *repositories.map { it.javaClass.simpleName.removeSuffix("Repository").decapitalize() to it }.toTypedArray()
    )

    override fun defaultRepository(): Repository = repositoryResolutionMap.values.firstOrNull()?:
            throw IllegalArgumentException("Could not resolve default repository.")

    override fun resolve(repositoryName: String): Repository = try {
        repositoryResolutionMap[repositoryName]?:
            defaultRepository()
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Could not resolve repository $repositoryName nor default repository", e)
    }
}