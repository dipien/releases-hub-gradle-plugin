package com.releaseshub.gradle.plugin.common

import java.io.InputStream
import java.net.URL

object ResourceUtils {

    fun getRequiredResource(name: String): URL {
        return ResourceUtils::class.java.classLoader.getResource(name)!!
    }

    fun getRequiredResourcePath(name: String): String {
        return ResourceUtils::class.java.classLoader.getResource(name)!!.file
    }

    fun getRequiredResourceAsStream(name: String): InputStream {
        return ResourceUtils::class.java.classLoader.getResourceAsStream(name)!!
    }
}