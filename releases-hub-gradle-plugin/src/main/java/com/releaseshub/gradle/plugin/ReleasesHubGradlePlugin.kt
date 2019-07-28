package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.task.ListDependenciesTask
import com.releaseshub.gradle.plugin.task.ListDependenciesToUpgradeTask
import com.releaseshub.gradle.plugin.task.UpgradeDependenciesTask
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
			listDependenciesTask.includes = extension.includes
			listDependenciesTask.excludes = extension.excludes
			listDependenciesTask.logLevel = extension.logLevel
		}

		val listDependenciesToUpgradeTask = project.tasks.create("listDependenciesToUpgrade", ListDependenciesToUpgradeTask::class.java)
		project.afterEvaluate {
			listDependenciesToUpgradeTask.dependenciesFilesPaths = extension.dependenciesFilesPaths
			listDependenciesToUpgradeTask.includes = extension.includes
			listDependenciesToUpgradeTask.excludes = extension.excludes
			listDependenciesToUpgradeTask.logLevel = extension.logLevel
		}

		val upgradeDependenciesTask = project.tasks.create("upgradeDependencies", UpgradeDependenciesTask::class.java)
		project.afterEvaluate {
			upgradeDependenciesTask.dependenciesFilesPaths = extension.dependenciesFilesPaths
			upgradeDependenciesTask.includes = extension.includes
			upgradeDependenciesTask.excludes = extension.excludes
			upgradeDependenciesTask.headBranch = extension.headBranch
			upgradeDependenciesTask.baseBranch = extension.baseBranch
			upgradeDependenciesTask.commitMessage = extension.commitMessage
			upgradeDependenciesTask.pullRequestTitle = extension.pullRequestTitle
			upgradeDependenciesTask.pullRequestEnabled = extension.pullRequestEnabled
			upgradeDependenciesTask.gitHubUserName = extension.gitHubUserName
			upgradeDependenciesTask.gitHubUserEmail = extension.gitHubUserEmail
			upgradeDependenciesTask.gitHubRepositoryOwner = extension.gitHubRepositoryOwner
			upgradeDependenciesTask.gitHubRepositoryName = extension.gitHubRepositoryName
			upgradeDependenciesTask.logLevel = extension.logLevel
		}
	}
}
