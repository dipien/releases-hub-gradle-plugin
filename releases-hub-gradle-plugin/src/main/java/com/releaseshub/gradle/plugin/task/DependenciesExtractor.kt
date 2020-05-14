package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File

object DependenciesExtractor {

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

            val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
            lines.forEach { line ->
                var artifact: ArtifactUpgrade? = null
                val matchResult = getDependencyMatchResult(line)
                if (matchResult != null) {
                    artifact = ArtifactUpgrade(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
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

    private fun extractGradle(rootDir: File, includes: List<String>?, excludes: List<String>?, dependenciesParserResult: DependenciesExtractorResult) {
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
