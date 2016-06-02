package io.github.knes1.kotao.brew.repositories

import io.github.knes1.kotao.brew.services.PageCollection

/**
 * @author knesek
 * Created on: 5/28/16
 */
interface AutoCollectionRepository : Repository {
    fun collections(): List<PageCollection>
}