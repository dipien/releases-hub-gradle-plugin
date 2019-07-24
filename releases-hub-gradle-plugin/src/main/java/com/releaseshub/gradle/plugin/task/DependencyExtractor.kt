package com.releaseshub.gradle.plugin.task

object DependencyExtractor {

	private val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()

	fun extractDependency(line: String): Dependency? {
		val matchResult = regex.matchEntire(line)
		if (matchResult != null) {
			return Dependency(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
		}
		return null
	}
}