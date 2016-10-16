package io.github.knes1.kotao.brew.services

/**
 * @author knesek
 * Created on: 10/16/16
 */
data class Paginator (
        val totalElements: Long,
        val pageSize: Long,
        val totalPages: Long = (totalElements / pageSize) + 1,
        val currentPage: Long
) {

    fun isFirst(): Boolean = currentPage == 1L

    fun isLast(): Boolean = !hasNext()

    fun hasNext(): Boolean = currentPage < totalPages

    fun next(): Long = currentPage + 1

    fun prev(): Long = currentPage - 1

    fun pageIndicesAround(pagesAround: Int = 2, anchorPage: Int = currentPage.toInt()): List<Int> {
        val total = pagesAround * 2 + 1
        val startIndex = Math.max(1, anchorPage - pagesAround)
        val endIndex = Math.min(totalPages.toInt(), startIndex + total)
        return (startIndex..endIndex).toList()
    }

}