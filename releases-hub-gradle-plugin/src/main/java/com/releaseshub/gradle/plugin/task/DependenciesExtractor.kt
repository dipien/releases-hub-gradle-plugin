package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File

object DependenciesExtractor {

    private val placeholderRegex = """const val ([^ ]+) = "([^"]+)"""".toRegex()
    private val dependenciesRegex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()
    private val gradleRegex = """.*/gradle-([^-]+)-.*""".toRegex()

    fun extractArtifacts(rootDir: File, dependenciesBasePath: String, dependenciesClassNames: List<String>, includes: List<String>? = null, excludes: List<String>? = null): DependenciesExtractorResult {
        val dependenciesParserResult = DependenciesExtractorResult()

        extractDependency(rootDir, dependenciesBasePath, dependenciesClassNames, includes, excludes, dependenciesParserResult)
        extractGradle(rootDir, includes, excludes, dependenciesParserResult)

        return dependenciesParserResult
    }

    private fun extractDependency(rootDir: File, dependenciesBasePath: String, dependenciesClassNames: List<String>, includes: List<String>?, excludes: List<String>?, dependenciesParserResult: DependenciesExtractorResult) {

        val basePath = if (dependenciesBasePath.endsWith(File.separator)) dependenciesBasePath else "$dependenciesBasePath${File.separator}"

        dependenciesClassNames.forEach { className ->
            val lines = File(rootDir, basePath + className).readLines()
            dependenciesParserResult.dependenciesLinesMap[basePath + className] = lines

            val placeholdersMap = mutableMapOf<String, String>()

            val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
            lines.forEach { line ->

                val placeholderMatchResult = getPlaceholderMatchResult(line)

                if (placeholderMatchResult != null) {
                    placeholdersMap[placeholderMatchResult.groupValues[1]] = placeholderMatchResult.groupValues[2]
                }

                var artifact: ArtifactUpgrade? = null
                val matchResult = getDependencyMatchResult(line)
                if (matchResult != null) {
                    val groupId = extractValue(matchResult.groupValues[1], placeholdersMap)
                    val artifactId = extractValue(matchResult.groupValues[2], placeholdersMap)
                    val version = extractValue(matchResult.groupValues[3], placeholdersMap)
                    artifact = ArtifactUpgrade(groupId, artifactId, version)
                }

                if (artifact != null) {
                    if (artifact.match(includes, excludes)) {
                        matchedArtifactsUpgrades.add(artifact)
                    } else {
                        dependenciesParserResult.excludedArtifacts.add(artifact)
                    }
                }
            }
            dependenciesParserResult.artifactsMap[basePath + className] = matchedArtifactsUpgrades.sortedBy { it.toString() }
        }
    }

    private fun extractValue(value: String, placeholdersMap: Map<String, String>): String {
        var result = value
        if (value.startsWith("\${") && value.endsWith("}")) {
            var key = value.removePrefix("\${")
            key = key.removeSuffix("}")
            result = placeholdersMap[key]!!
        }
        return result
    }

    private fun extractGradle(rootDir: File, includes: List<String> = emptyList(), excludes: List<String> = emptyList(), dependenciesParserResult: DependenciesExtractorResult) {
        val gradleWrapperFile = GradleHelper.getGradleWrapperPropertiesFile(rootDir)
        if (gradleWrapperFile.exists()) {
            val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
            val pathRelativeToRootProject = gradleWrapperFile.absolutePath.replaceFirst(rootDir.absolutePath + File.separator, "")
            gradleWrapperFile.forEachLine { line ->
                var artifact: ArtifactUpgrade? = null

                val matchResult = getGradleMatchResult(line)
                if (matchResult != null) {
                    artifact = ArtifactUpgrade(ArtifactUpgrade.GRADLE_ID, matchResult.groupValues[1])
                }

                artifact?.let { it ->
                    if (it.match(includes, excludes)) {
                        matchedArtifactsUpgrades.add(it)
                    } else {
                        dependenciesParserResult.excludedArtifacts.add(it)
                    }
                }
            }
            dependenciesParserResult.artifactsMap[pathRelativeToRootProject] = matchedArtifactsUpgrades.sortedBy { it.toString() }
        }
    }

    fun getPlaceholderMatchResult(line: String): MatchResult? {
        // TODO Add support to inline or multiline /* */ comments
        if (!line.trim().startsWith("//")) {
            return placeholderRegex.matchEntire(line.trim())
        }
        return null
    }

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
