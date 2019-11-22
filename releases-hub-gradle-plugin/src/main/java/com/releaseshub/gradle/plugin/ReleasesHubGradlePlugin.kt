package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.task.ListDependenciesTask
import com.releaseshub.gradle.plugin.task.ListDependenciesToUpgradeTask
import com.releaseshub.gradle.plugin.task.UpgradeDependenciesTask
import com.releaseshub.gradle.plugin.task.ValidateDependenciesTask
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

        val validateDependenciesTask = project.tasks.create(ValidateDependenciesTask.TASK_NAME, ValidateDependenciesTask::class.java)
        project.afterEvaluate {
            initTask(validateDependenciesTask)
            validateDependenciesTask.unusedExcludes = extension.unusedExcludes
            validateDependenciesTask.unusedExtensionsToSearch = extension.unusedExtensionsToSearch
        }

        val listDependenciesTask = project.tasks.create(ListDependenciesTask.TASK_NAME, ListDependenciesTask::class.java)
        project.afterEvaluate {
            initTask(listDependenciesTask)
        }

        val listDependenciesToUpgradeTask = project.tasks.create(ListDependenciesToUpgradeTask.TASK_NAME, ListDependenciesToUpgradeTask::class.java)
        project.afterEvaluate {
            initTask(listDependenciesToUpgradeTask)
        }

        val upgradeDependenciesTask = project.tasks.create(UpgradeDependenciesTask.TASK_NAME, UpgradeDependenciesTask::class.java)
        project.afterEvaluate {
            initTask(upgradeDependenciesTask)
            upgradeDependenciesTask.baseBranch = extension.baseBranch
            upgradeDependenciesTask.headBranchPrefix = extension.headBranchPrefix
            upgradeDependenciesTask.pullRequestEnabled = extension.pullRequestEnabled
            upgradeDependenciesTask.pullRequestsMax = extension.pullRequestsMax
            upgradeDependenciesTask.pullRequestLabels = extension.pullRequestLabels
            upgradeDependenciesTask.pullRequestReviewers = extension.pullRequestReviewers
            upgradeDependenciesTask.pullRequestTeamReviewers = extension.pullRequestTeamReviewers
            upgradeDependenciesTask.gitHubUserName = extension.gitHubUserName
            upgradeDependenciesTask.gitHubUserEmail = extension.gitHubUserEmail
            upgradeDependenciesTask.gitHubRepositoryOwner = extension.gitHubRepositoryOwner
            upgradeDependenciesTask.gitHubRepositoryName = extension.gitHubRepositoryName
            upgradeDependenciesTask.gitHubWriteToken = extension.gitHubWriteToken
            upgradeDependenciesTask.gitHubApiUrl = extension.gitHubApiHostName
        }
    }

    private fun initTask(task: AbstractTask) {
        task.serverName = extension.serverName
        task.userToken = extension.userToken
        task.dependenciesBasePath = extension.dependenciesBasePath
        task.dependenciesClassNames = extension.dependenciesClassNames
        task.includes = extension.includes
        task.excludes = extension.excludes
        task.logLevel = extension.logLevel
    }
}
