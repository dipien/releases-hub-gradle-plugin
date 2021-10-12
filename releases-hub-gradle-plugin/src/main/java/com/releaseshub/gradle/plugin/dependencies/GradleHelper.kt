package com.releaseshub.gradle.plugin.dependencies

import java.io.File

object GradleHelper {

    var GRADLE_WRAPPER_PROPERTIES_RELATIVE_PATH = "gradle" + File.separator + "wrapper" + File.separator + "gradle-wrapper.properties"

    fun getGradleWrapperPropertiesFile(rootDir: File): File {
        return File(rootDir.absolutePath + File.separator + GRADLE_WRAPPER_PROPERTIES_RELATIVE_PATH)
    }

    fun getGradleBatWrapperFile(rootDir: File): File {
        return File(rootDir.absolutePath + File.separator + "gradlew.bat")
    }
}
