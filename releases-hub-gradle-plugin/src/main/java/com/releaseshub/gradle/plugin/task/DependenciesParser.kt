package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact

object DependenciesParser {

    private val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

    fun extractArtifact(line: String): Artifact? {
        val matchResult = getMatchResult(line)
        if (matchResult != null) {
            return Artifact(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
        }
        return null
    }

    fun upgradeDependency(line: String, artifactsToUpgrade: List<Artifact>): UpgradeResult {
        val matchResult = getMatchResult(line)
        if (matchResult != null) {
            val artifact = artifactsToUpgrade.find {
                it.groupId == matchResult.groupValues[1] && it.artifactId == matchResult.groupValues[2]
            }
            if (artifact != null) {
                val oldVersion = matchResult.groupValues[3]
                val newLine = line.replaceFirst(oldVersion, artifact.toVersion!!)
                return UpgradeResult(true, oldVersion, artifact, newLine)
            }
        }
        return UpgradeResult(false, null, null, line)
    }

    private fun getMatchResult(line: String): MatchResult? {
        if (!line.trim().startsWith("//")) {
            return regex.matchEntire(line)
        }
        return null
    }
}