package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact

object ArtifactExtractor {

	private val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

	fun extractArtifact(line: String): Artifact? {
		val matchResult = regex.matchEntire(line)
		if (matchResult != null) {
			return Artifact(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
		}
		return null
	}
}