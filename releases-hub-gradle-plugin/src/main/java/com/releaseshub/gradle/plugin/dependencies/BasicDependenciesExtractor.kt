package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File

class BasicDependenciesExtractor(private val dependenciesPaths: List<String>) : DependenciesExtractor {

    override fun extractArtifacts(rootDir: File, includes: List<String>?, excludes: List<String>?): DependenciesExtractorResult {
        val dependenciesParserResult = DependenciesExtractorResult()
        dependenciesPaths.forEach { path ->
            val file = File(rootDir, path)
            if (file.exists()) {
                dependenciesParserResult.dependenciesFiles.add(file)
                if (file.absolutePath == GradleHelper.getGradleWrapperPropertiesFile(rootDir).absolutePath) {
                    extractGradle(file, path, includes, excludes, dependenciesParserResult)
                } else {
                    extractMavenDependencies(file, path, includes, excludes, dependenciesParserResult)
                }
            }
        }
        return dependenciesParserResult
    }

    private fun extractMavenDependencies(file: File, relativePath: String, includes: List<String>?, excludes: List<String>?, dependenciesParserResult: DependenciesExtractorResult) {
        val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
        file.forEachLine { line ->
            var artifact: ArtifactUpgrade? = null
            var matchResult = DependenciesExtractor.getDependencyMatchResult(line)
            if (matchResult != null) {
                artifact = ArtifactUpgrade(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
            } else {
                matchResult = DependenciesExtractor.getPluginsDSLMatchResult(line)
                if (matchResult != null) {
                    artifact = ArtifactUpgrade(matchResult.groupValues[1], true, matchResult.groupValues[2])
                }
            }

            if (artifact != null) {
                if (artifact.match(includes, excludes)) {
                    matchedArtifactsUpgrades.add(artifact)
                } else {
                    dependenciesParserResult.excludedArtifacts.add(artifact)
                }
            }
        }
        dependenciesParserResult.artifactsMap[relativePath] = matchedArtifactsUpgrades.sortedBy { it.toString() }
    }

    private fun extractGradle(file: File, relativePath: String, includes: List<String>?, excludes: List<String>?, dependenciesParserResult: DependenciesExtractorResult) {
        val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
        file.forEachLine { line ->
            var artifact: ArtifactUpgrade? = null

            val matchResult = DependenciesExtractor.getGradleMatchResult(line)
            if (matchResult != null) {
                artifact = ArtifactUpgrade(ArtifactUpgrade.GRADLE_ID, matchResult.groupValues[1])
            }

            if (artifact != null) {
                if (artifact.match(includes, excludes)) {
                    matchedArtifactsUpgrades.add(artifact)
                } else {
                    dependenciesParserResult.excludedArtifacts.add(artifact)
                }
            }
        }
        dependenciesParserResult.artifactsMap[relativePath] = matchedArtifactsUpgrades.sortedBy { it.toString() }
    }
}
