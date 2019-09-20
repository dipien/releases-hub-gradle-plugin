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

    fun upgradeDependency(line: String, artifactsToUpgrade: List<ArtifactUpgrade>): UpgradeResult {
        val matchResult = getMatchResult(line)
        if (matchResult != null) {
            val artifact = artifactsToUpgrade.find {
                it.groupId == matchResult.groupValues[1] && it.artifactId == matchResult.groupValues[2]
            }
            if (artifact != null) {
                val newLine = line.replaceFirst(artifact.fromVersion!!, artifact.toVersion!!)
                return UpgradeResult(true, artifact, newLine)
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