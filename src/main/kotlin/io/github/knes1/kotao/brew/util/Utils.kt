package io.github.knes1.kotao.brew.util

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

    }
}