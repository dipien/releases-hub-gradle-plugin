package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.task.ListDependenciesTask
import org.gradle.api.Plugin
import org.gradle.api.Project


class ReleasesHubGradlePlugin : Plugin<Project> {

	companion object {
		const val EXTENSION_NAME = "releasesHub"
	}

	lateinit var extension: ReleasesHubGradlePluginExtension
		private set

	override fun apply(project: Project) {
		extension = project.extensions.create(EXTENSION_NAME, ReleasesHubGradlePluginExtension::class.java, project)

		val listDependenciesTask = project.tasks.create("listDependencies", ListDependenciesTask::class.java)
		project.afterEvaluate {
			listDependenciesTask.dependenciesFilesPaths = extension.dependenciesFilesPaths
		}
	}
}
