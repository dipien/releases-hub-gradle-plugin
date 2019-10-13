package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.gradle.api.Project
import java.io.File

object DependenciesParser {

    private val dependenciesRegex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()
    private val gradleRegex = """.*/gradle-([^-]+)-.*""".toRegex()

    const val GRADLE_FILE_NAME = "gradle-wrapper.properties"

    fun extractArtifacts(project: Project, dependenciesBasePath: String, dependenciesClassNames: List<String>, includes: List<String>, excludes: List<String>): DependenciesParserResult {
        val dependenciesParserResult = DependenciesParserResult()

        // Dependencies
        dependenciesClassNames.forEach {
            val lines = project.rootProject.file(dependenciesBasePath + it).readLines()
            dependenciesParserResult.dependenciesLinesMap[dependenciesBasePath + it] = lines

            lines.forEach { line ->
                val artifact = extractArtifact(line)
                if (artifact != null) {
                    if (artifact.match(includes, excludes)) {
                        dependenciesParserResult.artifactsMap.putIfAbsent(dependenciesBasePath + it, mutableListOf())
                        dependenciesParserResult.artifactsMap[dependenciesBasePath + it]!!.add(artifact)
                    } else {
                        dependenciesParserResult.excludedArtifacts.add(artifact)
                    }
                }
            }
        }

        // Gradle
        File(project.rootProject.projectDir.absolutePath).walk().forEach {file ->
            if (file.name == GRADLE_FILE_NAME) {
                file.forEachLine {line ->
                   val artifact = extractGradleArtifact(line)
                    if (artifact != null) {
                        val pathRelativeToRootProject = file.absolutePath.replaceFirst(project.rootProject.projectDir.absolutePath + "/", "")
                        dependenciesParserResult.artifactsMap.putIfAbsent(pathRelativeToRootProject, mutableListOf())
                        dependenciesParserResult.artifactsMap[pathRelativeToRootProject]!!.add(artifact)
                    }
                }
            }
        }

        return dependenciesParserResult
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