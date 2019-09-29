package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.gradle.api.Project

object DependenciesParser {

    private val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

    fun extractArtifacts(project: Project, dependenciesBasePath: String, dependenciesClassNames: List<String>, includes: List<String>, excludes: List<String>): DependenciesParserResult {
        val dependenciesParserResult = DependenciesParserResult()
        dependenciesClassNames.forEach {
            val lines = project.rootProject.file(dependenciesBasePath + it).readLines()
            dependenciesParserResult.dependenciesMap[dependenciesBasePath + it] = lines

            lines.forEach { line ->
                val artifact = extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    dependenciesParserResult.artifacts.add(artifact)
                }
            }
        }
        return dependenciesParserResult
    }

    fun extractArtifact(line: String): ArtifactUpgrade? {
        val matchResult = getMatchResult(line)
        if (matchResult != null) {
            return ArtifactUpgrade(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
        }
        return null
    }

    fun upgradeDependency(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        val matchResult = getMatchResult(line)
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

    private fun getMatchResult(line: String): MatchResult? {
        if (!line.trim().startsWith("//")) {
            return regex.matchEntire(line)
        }
        return null
    }
}