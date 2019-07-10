package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.common.PropertyResolver

import org.gradle.api.Project


open class ReleasesHubGradlePluginExtension(project: Project) {

	private val propertyResolver: PropertyResolver = PropertyResolver(project)

}
