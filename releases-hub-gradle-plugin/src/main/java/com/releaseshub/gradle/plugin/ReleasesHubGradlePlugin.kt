package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.artifacts.fetch.MavenArtifactRepository
import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.task.ListDependenciesTask
import com.releaseshub.gradle.plugin.task.ListDependenciesToUpgradeTask
import com.releaseshub.gradle.plugin.task.UpgradeDependenciesTask
import com.releaseshub.gradle.plugin.task.ValidateDependenciesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ReleasesHubGradlePlugin : Plugin<Project> {

    companion object {
        const val EXTENSION_NAME = "releasesHub"
    }

    lateinit var extension: ReleasesHubGradlePluginExtension
        private set

    override fun apply(project: Project) {
        extension = project.extensions.create(EXTENSION_NAME, ReleasesHubGradlePluginExtension::class.java, project)

        val validateDependenciesTask = project.tasks.create(ValidateDependenciesTask.TASK_NAME, ValidateDependenciesTask::class.java)
        validateDependenciesTask.notCompatibleWithConfigurationCache("Not implemented yet")
        project.afterEvaluate {
            initTask(project, validateDependenciesTask)
            validateDependenciesTask.unusedExcludes = extension.unusedExcludes
            validateDependenciesTask.unusedExtensionsToSearch = extension.unusedExtensionsToSearch

            val projectsDirs = mutableListOf<File>()
            project.rootProject.allprojects.forEach {
                projectsDirs.add(it.projectDir)
            }
            validateDependenciesTask.projectsDirs = projectsDirs
        }

        val listDependenciesTask = project.tasks.create(ListDependenciesTask.TASK_NAME, ListDependenciesTask::class.java)
        listDependenciesTask.notCompatibleWithConfigurationCache("Not implemented yet")
        project.afterEvaluate {
            initTask(project, listDependenciesTask)
        }

        val listDependenciesToUpgradeTask = project.tasks.create(ListDependenciesToUpgradeTask.TASK_NAME, ListDependenciesToUpgradeTask::class.java)
        listDependenciesToUpgradeTask.notCompatibleWithConfigurationCache("Not implemented yet")
        project.afterEvaluate {
            initTask(project, listDependenciesToUpgradeTask)
        }

        val upgradeDependenciesTask = project.tasks.create(UpgradeDependenciesTask.TASK_NAME, UpgradeDependenciesTask::class.java)
        upgradeDependenciesTask.notCompatibleWithConfigurationCache("Not implemented yet")
        project.afterEvaluate {
            initTask(project, upgradeDependenciesTask)
            upgradeDependenciesTask.baseBranch = extension.baseBranch
            upgradeDependenciesTask.headBranchPrefix = extension.headBranchPrefix
            upgradeDependenciesTask.pullRequestEnabled = extension.pullRequestEnabled
            upgradeDependenciesTask.pullRequestsMax = extension.pullRequestsMax
            upgradeDependenciesTask.pullRequestLabels = extension.pullRequestLabels
            upgradeDependenciesTask.pullRequestReviewers = extension.pullRequestReviewers
            upgradeDependenciesTask.pullRequestAssignee = extension.pullRequestAssignee
            upgradeDependenciesTask.pullRequestTeamReviewers = extension.pullRequestTeamReviewers
            upgradeDependenciesTask.gitUserName = extension.gitUserName
            upgradeDependenciesTask.gitUserEmail = extension.gitUserEmail
            upgradeDependenciesTask.gitHubRepository = extension.gitHubRepository
            upgradeDependenciesTask.gitHubRepositoryOwner = extension.gitHubRepositoryOwner
            upgradeDependenciesTask.gitHubRepositoryName = extension.gitHubRepositoryName
            upgradeDependenciesTask.gitHubWriteToken = extension.gitHubWriteToken
            upgradeDependenciesTask.gitHubApiHostName = extension.gitHubApiHostName
        }
    }

    private fun initTask(project: Project, task: AbstractTask) {
        task.rootProjectDir = project.rootProject.projectDir
        task.rootProjectBuildDir = project.rootProject.buildDir
        task.dependenciesPaths = extension.dependenciesPaths
        task.autoDetectDependenciesPaths = extension.autoDetectDependenciesPaths
        task.includes = extension.includes
        task.excludes = extension.excludes
        task.logLevel = extension.logLevel

        val repositories = mutableListOf<MavenArtifactRepository>()
        project.repositories.plus(project.buildscript.repositories).forEach {
            if (it is org.gradle.api.artifacts.repositories.MavenArtifactRepository) {
                if (it.url.scheme == "http" || it.url.scheme == "https") {
                    val url = it.url.toString().dropLastWhile { char -> char == '/' }
                    repositories.add(MavenArtifactRepository(it.name, url))
                }
            }
        }
        task.repositories = repositories.distinctBy { it.url }

        project.rootProject.allprojects {
            task.buildFileAbsolutePaths.add(it.buildFile.absolutePath)
        }
    }
}
