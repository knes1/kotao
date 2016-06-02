package io.github.knes1.kotao.brew.services

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface ProcessorResolver {

    fun resolve(name: String): Processor?

}