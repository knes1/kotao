package io.github.knes1.kotao.brew.util

import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


/**
 * @author knesek
 * Created on: 10/16/16
 */
class Utils {
    companion object {

        fun normalizePath(path: String) =
                if (path.isNotEmpty() && !path.endsWith("/")) {
                    path + "/"
                } else {
                    path
                }

        /**
         * Recursively copies files from classpath resource folder to destination directory in filesystem
         */
        fun copyDirFromResources(srcResourceDir: String, destDir: File, overwrite: Boolean = false) {


            // Unfortunately, copying classpath resources needs to be done differently when application is started
            // from an IDE vs if it's started from JAR file. We need to detect where are we started from and
            // then create different type of Path.
            // Reference: https://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar

            val path = Utils::class.java.protectionDomain.codeSource.location.toURI().path
            val srcPath = if (path.toString().endsWith("jar")) {
                // We are started from JAR, hence we need to copy files from JAR using JAR filesystem
                val jarUri = URI.create("jar:file:" + path)
                val fs = FileSystems.newFileSystem(jarUri, emptyMap<String, Any>())
                fs.getPath(srcResourceDir)
            } else {
                // We're started from IDE, we can use normal file system to copy resources
                val classLoader = Thread.currentThread().contextClassLoader
                val classPathResourcePath = classLoader.getResource(srcResourceDir.removePrefix("/"))?.path?:
                        throw IllegalArgumentException("Internal error: Could not locate internal resource $srcResourceDir")
                FileSystems.getDefault().getPath(classPathResourcePath)
            }

            if (!Files.exists(srcPath) || !Files.isDirectory(srcPath)) throw IllegalArgumentException("$srcResourceDir does not exist or is not a directory.")
            val copyOptions = if (overwrite) arrayOf(StandardCopyOption.REPLACE_EXISTING) else emptyArray()

            //recursive copy
            Files.walk(srcPath).forEach { srcItem ->
                val destPath = destDir.toPath().resolve(srcPath.relativize(srcItem).toString())
                if (srcItem.isDirectory()) {
                    if (!destPath.exists()) {
                        destPath.createDirectory()
                    }
                } else {
                    Files.copy(srcItem, destPath, *copyOptions)
                }
            }
        }

        // Helper functions to make java.nio.Files methods available on Paths and make code more readable.
        fun Path.exists() = Files.exists(this)

        fun Path.isDirectory() = Files.isDirectory(this)

        fun Path.createDirectory() {
            Files.createDirectory(this)
        }
    }

}



