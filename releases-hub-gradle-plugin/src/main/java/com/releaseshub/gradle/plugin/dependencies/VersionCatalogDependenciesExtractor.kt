package com.releaseshub.gradle.plugin.dependencies

import java.io.File

// TODO This is a WIP class for https://github.com/dipien/releases-hub-gradle-plugin/issues/17
class VersionCatalogDependenciesExtractor : DependenciesExtractor {

    companion object {
        private val versionRegex = """\s*([^=\s]+)\s*=\s*"([^"]+)".*""".toRegex()
        private val libraryRegex = """\s*([^=\s]+)\s*=\s*\{ module = "([^:]+):([^"]+)".*""".toRegex()
    }

    override fun extractArtifacts(rootDir: File, includes: List<String>?, excludes: List<String>?): DependenciesExtractorResult {
        val catalog = File(rootDir, "gradle" + File.separator + "libs.versions.toml")
        val result = DependenciesExtractorResult()
        val versionsMap = mutableMapOf<String, String>()
        var onVersions = false
        var onLibraries = false
        catalog.readLines().forEach { line ->
            if (line.trim() == "[versions]") {
                onVersions = true
            } else if (line.trim() == "[libraries]") {
                onVersions = false
                onLibraries = true
            } else if (onVersions) {
                val matchResult = versionRegex.matchEntire(line)
                if (matchResult != null) {
                    versionsMap[matchResult.groupValues[1]] = matchResult.groupValues[1]
                }
            } else if (onLibraries) {
            }
        }

        return result
    }
}
