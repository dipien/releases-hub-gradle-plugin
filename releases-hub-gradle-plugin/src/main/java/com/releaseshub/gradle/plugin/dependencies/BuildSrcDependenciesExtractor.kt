package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File

class BuildSrcDependenciesExtractor(private val dependenciesPaths: List<String>) : DependenciesExtractor {

    override fun extractArtifacts(rootDir: File, includes: List<String>?, excludes: List<String>?): DependenciesExtractorResult {
        val dependenciesParserResult = DependenciesExtractorResult()
        extractDependency(rootDir, includes, excludes, dependenciesParserResult)
        extractGradle(rootDir, includes, excludes, dependenciesParserResult)
        return dependenciesParserResult
    }

    private fun extractDependency(rootDir: File, includes: List<String>?, excludes: List<String>?, dependenciesParserResult: DependenciesExtractorResult) {

        dependenciesPaths.forEach { path ->
            val file = File(rootDir, path)
            if (file.exists()) {
                val lines = file.readLines()
                dependenciesParserResult.dependenciesFiles.add(file)

                val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
                lines.forEach { line ->
                    var artifact: ArtifactUpgrade? = null
                    var matchResult = DependenciesExtractor.getDependencyMatchResult(line)
                    if (matchResult != null) {
                        artifact = ArtifactUpgrade(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
                    } else {
                        matchResult = DependenciesExtractor.getPluginsMatchResult(line)
                        if (matchResult != null) {
                            val pluginId = matchResult.groupValues[1]
                            // TODO Find a way to automatically map all the plugin ids to a groupId:artifactId
                            if (pluginId == "com.gradle.enterprise") {
                                artifact = ArtifactUpgrade("com.gradle", "gradle-enterprise-gradle-plugin", matchResult.groupValues[2])
                            }
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

                dependenciesParserResult.artifactsMap[path] = matchedArtifactsUpgrades.sortedBy { it.toString() }
            }
        }
    }

    private fun extractGradle(rootDir: File, includes: List<String>?, excludes: List<String>?, dependenciesParserResult: DependenciesExtractorResult) {
        val gradleWrapperFile = GradleHelper.getGradleWrapperPropertiesFile(rootDir)
        if (gradleWrapperFile.exists()) {
            val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
            val pathRelativeToRootProject = gradleWrapperFile.absolutePath.replaceFirst(rootDir.absolutePath + File.separator, "")
            gradleWrapperFile.forEachLine { line ->
                var artifact: ArtifactUpgrade? = null

                val matchResult = DependenciesExtractor.getGradleMatchResult(line)
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
}
