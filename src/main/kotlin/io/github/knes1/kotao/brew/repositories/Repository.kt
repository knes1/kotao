package io.github.knes1.kotao.brew.repositories

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface Repository {

    fun name(): String

    fun findAll(name: String): Sequence<Map<String, Any>>

    open fun count(name: String): Long
    open fun find(name: String, pageStart: Long, pageSize: Long): Sequence<Map<String, Any>>
}