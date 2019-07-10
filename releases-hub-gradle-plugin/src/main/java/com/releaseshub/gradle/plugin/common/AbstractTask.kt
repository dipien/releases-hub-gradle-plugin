package com.releaseshub.gradle.plugin.common

import com.releaseshub.gradle.plugin.ReleasesHubGradlePlugin
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


abstract class AbstractTask : DefaultTask() {

	protected lateinit var propertyResolver: PropertyResolver

	@TaskAction
	fun doExecute() {
		propertyResolver = PropertyResolver(project)
		onExecute()
	}

	protected fun getExtension(): ReleasesHubGradlePluginExtension {
		return project.extensions.getByName(ReleasesHubGradlePlugin.EXTENSION_NAME) as ReleasesHubGradlePluginExtension
	}

	protected abstract fun onExecute()

}
