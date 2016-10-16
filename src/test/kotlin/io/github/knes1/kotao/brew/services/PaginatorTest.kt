package io.github.knes1.kotao.brew.services

import org.junit.Assert.*
import org.junit.Test

import org.junit.Assert.*

/**
 * @author knesek
 * * Created on: 10/16/16
 */
class PaginatorTest {

    val paginator = Paginator(
            totalElements = 21,
            pageSize = 5,
            currentPage = 1
    )

    @Test
    fun testIsFirst() {
        assertTrue(paginator.isFirst())
        assertFalse(paginator.copy(currentPage = 2).isFirst())
    }

    @Test
    fun testIsLast() {
        assertFalse(paginator.isLast())
        assertTrue(paginator.copy(currentPage = 5).isLast())
    }

    @Test
    fun testHasNext() {
        assertTrue(paginator.hasNext())
        assertFalse(paginator.copy(currentPage = 5).hasNext())
    }

    @Test
    fun testPageIndicesAround() {
        assertEquals(listOf(1,2,3,4,5), paginator.pageIndicesAround(2))
    }
}