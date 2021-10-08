package com.releaseshub.gradle.plugin.dependencies

import java.io.File

interface DependenciesExtractor {

    companion object {

        private val dependenciesRegex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()
        private val gradleRegex = """.*/gradle-([^-]+)-.*""".toRegex()

        fun getDependencyMatchResult(line: String): MatchResult? {
            // TODO Add support to inline or multiline /* */ comments
            if (!line.trim().startsWith("//")) {
                return dependenciesRegex.matchEntire(line)
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
