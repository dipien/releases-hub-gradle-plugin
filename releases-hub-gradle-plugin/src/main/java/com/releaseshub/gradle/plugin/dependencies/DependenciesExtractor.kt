package com.releaseshub.gradle.plugin.dependencies

import java.io.File

interface DependenciesExtractor {

    companion object {

        private val dependenciesRegex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

        // For example: id("com.gradle.enterprise").version("3.7")
        // For example: id("com.jfrog.bintray") version "1.8.5"
        private val pluginsRegex = """.*id\("([^"]+)".*version.*"([^"]+)".*""".toRegex()

        private val gradleRegex = """.*/gradle-([^-]+)-.*""".toRegex()

        fun getDependencyMatchResult(line: String): MatchResult? {
            // TODO Add support to inline or multiline /* */ comments
            if (!line.trim().startsWith("//")) {
                return dependenciesRegex.matchEntire(line)
            }
            return null
        }

        fun getPluginsMatchResult(line: String): MatchResult? {
            // TODO Add support to inline or multiline /* */ comments
            if (!line.trim().startsWith("//")) {
                return pluginsRegex.matchEntire(line)
            }
            return null
        }

        fun getGradleMatchResult(line: String): MatchResult? {
            if (line.trim().startsWith("distributionUrl")) {
                return gradleRegex.matchEntire(line)
            }
            return null
        }
    }

    fun extractArtifacts(rootDir: File, includes: List<String>? = null, excludes: List<String>? = null): DependenciesExtractorResult
}
