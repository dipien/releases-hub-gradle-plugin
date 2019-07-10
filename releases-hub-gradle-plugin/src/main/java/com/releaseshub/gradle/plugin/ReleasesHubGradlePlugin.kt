package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.task.ListDependenciesTask

import org.gradle.api.Plugin
import org.gradle.api.Project


class ReleasesHubGradlePlugin : Plugin<Project> {

	companion object {
		const val EXTENSION_NAME = "releasesHub"
	}

	override fun apply(project: Project) {
		project.extensions.create(EXTENSION_NAME, ReleasesHubGradlePluginExtension::class.java, project)
		project.tasks.create("listDependencies", ListDependenciesTask::class.java)
	}
}
