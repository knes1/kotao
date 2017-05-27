package io.github.knes1.kotao.brew.services

/**
 *
 * Generator service performs the following:
 *
 * - Loads configuration
 * - Prepares the model
 *   - Loads all collections into the model
 *   -
 *
 *
 * @author knesek
 * Created on: 5/25/16
 */
interface Generator {

    fun generateAll()
    fun processAssets()
    fun generatePages()

}