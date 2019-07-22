package com.releaseshub.gradle.plugin.task

object DependencyExtractor {

	fun extractDependency(line: String): Dependency? {
		val regex = """.*"([^:]+):([^:]+):([^:]+)".*""".toRegex()
		val matchResult = regex.matchEntire(line)
		if (matchResult != null) {
			return Dependency(matchResult.groupValues[1], matchResult.groupValues[2], matchResult.groupValues[3])
		}
		return null
	}
}