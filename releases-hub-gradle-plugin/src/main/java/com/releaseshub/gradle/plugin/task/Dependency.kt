package com.releaseshub.gradle.plugin.task

data class Dependency(val groupId: String, val artifactId: String, val version: String) {

	fun match(includes : List<String>, excludes : List<String>) : Boolean {
		val includeMatches = includes.isEmpty() || includes.find { match(it) } != null
		return if (includeMatches) {
			excludes.find { match(it) } == null
		} else {
			false
		}
	}

	private fun match(expression: String) : Boolean {
		val split = expression.split(":")
		val groupIdToMatch = split[0]
		val artifactIdToMatch = if (split.size > 1) split[1] else null
		return groupIdToMatch == groupId && (artifactIdToMatch == null || artifactIdToMatch == artifactId)
	}
}