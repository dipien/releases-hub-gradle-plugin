package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.common.PropertyResolver

import org.gradle.api.Project


open class ReleasesHubGradlePluginExtension(project: Project) {

	private val propertyResolver: PropertyResolver = PropertyResolver(project)

	var dependenciesFilesPaths = mutableListOf<String>()
	var includes = mutableListOf<String>()
	var excludes = mutableListOf<String>()

}
