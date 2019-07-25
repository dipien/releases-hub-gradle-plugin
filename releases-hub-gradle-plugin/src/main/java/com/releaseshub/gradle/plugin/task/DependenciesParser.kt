package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact

object DependenciesParser {

	private val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

	fun extractArtifact(line: String): Artifact? {
		val matchResult = regex.matchEntire(line)
		if (matchResult != null) {
			return Artifact(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
		}
		return null
	}

	fun upgradeDependency(line: String, artifactsToUpgrade: List<Artifact>): UpgradeResult {
		// TODO
		return UpgradeResult(false, null, null, line)
	}
}