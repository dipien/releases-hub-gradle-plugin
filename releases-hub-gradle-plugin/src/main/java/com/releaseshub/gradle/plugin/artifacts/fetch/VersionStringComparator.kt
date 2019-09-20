package com.releaseshub.gradle.plugin.artifacts.fetch

object VersionStringComparator : Comparator<String> {

    override fun compare(version1: String?, version2: String?): Int {
        return when {
            version1 == null && version2 == null -> 0
            version2 == null -> -1
            version1 == null -> 1
            version1.toIntOrNull() != null && version2.toIntOrNull() != null -> version1.toInt().compareTo(version2.toInt())
            Character.isDigit(version1[0]) && !Character.isDigit(version2[0]) -> 1
            !Character.isDigit(version1[0]) && Character.isDigit(version2[0]) -> -1
            version1 > version2 -> 1
            version1 < version2 -> -1
            else -> 0
        }
    }
}