package com.releaseshub.gradle.plugin.task

import java.io.File

object GradleHelper {

    fun getGradleWrapperPropertiesFile(rootDir: File): File {
        return File(rootDir.absolutePath + File.separator + "gradle" + File.separator + "wrapper" + File.separator + "gradle-wrapper.properties")
    }

    fun getGradleBatWrapperFile(rootDir: File): File {
        return File(rootDir.absolutePath + File.separator + "gradlew.bat")
    }
}
