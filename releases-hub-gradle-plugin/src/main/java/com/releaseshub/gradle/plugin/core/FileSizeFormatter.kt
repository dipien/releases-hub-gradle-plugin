package com.releaseshub.gradle.plugin.core

import java.text.DecimalFormat

object FileSizeFormatter {

    fun format(sizeInBytes: Long): String {
        return when {
            sizeInBytes < 1024 -> {
                "$sizeInBytes bytes"
            }
            sizeInBytes < 1048576 -> {
                "${DecimalFormat("#.##").format(sizeInBytes.div(1024f))} KB"
            }
            else -> {
                "${DecimalFormat("#.##").format(sizeInBytes.div(1048576f))} MB"
            }
        }
    }
}
