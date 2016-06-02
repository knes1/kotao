package io.github.knes1.kotao.brew.services

import java.io.InputStream
import java.io.OutputStream

/**
 * @author knesek
 * Created on: 5/25/16
 */
interface Processor {
    fun process(input: String, output: OutputStream)
}