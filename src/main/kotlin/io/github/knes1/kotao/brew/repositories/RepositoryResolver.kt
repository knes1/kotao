package io.github.knes1.kotao.brew.repositories

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface RepositoryResolver {

    fun resolve(repositoryName: String): Repository
    fun defaultRepository(): Repository

}