package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File
import org.gradle.api.Project

object DependenciesParser {

    private val dependenciesRegex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()
    private val gradleRegex = """.*/gradle-([^-]+)-.*""".toRegex()

    fun extractArtifacts(project: Project, dependenciesBasePath: String, dependenciesClassNames: List<String>, includes: List<String>, excludes: List<String>): DependenciesParserResult {
        val dependenciesParserResult = DependenciesParserResult()

        // Dependencies
        dependenciesClassNames.forEach { className ->
            val lines = project.rootProject.file(dependenciesBasePath + className).readLines()
            dependenciesParserResult.dependenciesLinesMap[dependenciesBasePath + className] = lines

            val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
            lines.forEach { line ->
                val artifact = extractArtifact(line)
                if (artifact != null) {
                    if (artifact.match(includes, excludes)) {
                        matchedArtifactsUpgrades.add(artifact)
                    } else {
                        dependenciesParserResult.excludedArtifacts.add(artifact)
                    }
                }
            }
            dependenciesParserResult.artifactsMap[dependenciesBasePath + className] = matchedArtifactsUpgrades.sortedBy { it.toString() }
        }

        // Gradle
        val gradleWrapperFile = getGradleWrapperFile(project)
        if (gradleWrapperFile.exists()) {
            val matchedArtifactsUpgrades = mutableListOf<ArtifactUpgrade>()
            val pathRelativeToRootProject = gradleWrapperFile.absolutePath.replaceFirst(project.rootProject.projectDir.absolutePath + File.separator, "")
            gradleWrapperFile.forEachLine { line ->
                val artifact = extractGradleArtifact(line)
                if (artifact != null) {
                    if (artifact.match(includes, excludes)) {
                        matchedArtifactsUpgrades.add(artifact)
                    } else {
                        dependenciesParserResult.excludedArtifacts.add(artifact)
                    }
                }
            }
            dependenciesParserResult.artifactsMap[pathRelativeToRootProject] = matchedArtifactsUpgrades.sortedBy { it.toString() }
        }

        return dependenciesParserResult
    }

    fun getGradleWrapperFile(project: Project): File {
        return File(project.rootProject.projectDir.absolutePath + File.separator + "gradle" + File.separator + "wrapper" + File.separator + "gradle-wrapper.properties")
    }

    fun extractArtifact(line: String): ArtifactUpgrade? {
        val matchResult = getDependencyMatchResult(line)
        if (matchResult != null) {
            return ArtifactUpgrade(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
        }
        return null
    }

    fun extractGradleArtifact(line: String): ArtifactUpgrade? {
        val matchResult = getGradleMatchResult(line)
        if (matchResult != null) {
            return ArtifactUpgrade(ArtifactUpgrade.GRADLE_ID, matchResult.groupValues[1])
        }
        return null
    }

    fun upgradeDependency(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        val matchResult = getDependencyMatchResult(line)
        if (matchResult != null) {
            if (artifactToUpgrade.groupId == matchResult.groupValues[1] && artifactToUpgrade.artifactId == matchResult.groupValues[2]) {
                val newLine = line.replaceFirst(matchResult.groupValues[3], artifactToUpgrade.toVersion!!)
                if (newLine != line) {
                    artifactToUpgrade.fromVersion = matchResult.groupValues[3]
                    return UpgradeResult(true, artifactToUpgrade, newLine)
                }
            }
        }
        return UpgradeResult(false, null, line)
    }

    fun upgradeGradle(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        val matchResult = getGradleMatchResult(line)
        if (matchResult != null) {
            if (artifactToUpgrade.id == ArtifactUpgrade.GRADLE_ID) {
                val newLine = line.replaceFirst(matchResult.groupValues[1], artifactToUpgrade.toVersion!!)
                if (newLine != line) {
                    artifactToUpgrade.fromVersion = matchResult.groupValues[1]
                    return UpgradeResult(true, artifactToUpgrade, newLine)
                }
            }
        }
        return UpgradeResult(false, null, line)
    }

    private fun getDependencyMatchResult(line: String): MatchResult? {
        if (!line.trim().startsWith("//")) {
            return dependenciesRegex.matchEntire(line)
        }
        return null
    }

    private fun getGradleMatchResult(line: String): MatchResult? {
        if (line.trim().startsWith("distributionUrl")) {
            return gradleRegex.matchEntire(line)
        }
        return null
    }
}
