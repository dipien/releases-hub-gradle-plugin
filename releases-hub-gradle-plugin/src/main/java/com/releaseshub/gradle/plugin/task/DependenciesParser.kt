package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade

object DependenciesParser {

    private val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

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
                val newLine = line.replaceFirst(artifactToUpgrade.fromVersion!!, artifactToUpgrade.toVersion!!)
                return UpgradeResult(true, artifactToUpgrade, newLine)
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