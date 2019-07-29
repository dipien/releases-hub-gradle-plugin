package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.common.PropertyResolver

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

open class ReleasesHubGradlePluginExtension(project: Project) {

    private val propertyResolver: PropertyResolver = PropertyResolver(project)

    var dependenciesFilesPaths: List<String>?
    var includes: List<String>
    var excludes: List<String>

    var headBranch: String?
    var baseBranch: String?
    var commitMessage: String?
    var pullRequestTitle: String?
    var pullRequestEnabled: Boolean

    var gitHubUserName: String?
    var gitHubUserEmail: String?
    var gitHubRepositoryOwner: String?
    var gitHubRepositoryName: String?
    var gitHubWriteToken: String?

    var logLevel = LogLevel.LIFECYCLE

    init {
        dependenciesFilesPaths = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::dependenciesFilesPaths.name, listOf())
        includes = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::includes.name, listOf()) ?: listOf()
        excludes = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::includes.name, listOf()) ?: listOf()

        headBranch = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::headBranch.name, "dependencies_upgrade")
        baseBranch = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::baseBranch.name, "master")
        commitMessage = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::commitMessage.name, "Upgraded dependencies")
        pullRequestTitle = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::pullRequestTitle.name, "Upgraded dependencies")
        pullRequestEnabled = propertyResolver.getBooleanProp(ReleasesHubGradlePluginExtension::pullRequestEnabled.name, false) ?: false

        gitHubUserName = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubUserName.name)
        gitHubUserEmail = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubUserEmail.name)

        gitHubRepositoryOwner = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubRepositoryOwner.name)
        gitHubRepositoryName = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubRepositoryName.name)
        gitHubWriteToken = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubWriteToken.name)
    }

    fun validateDependenciesFilesPaths() {
        require(!dependenciesFilesPaths.isNullOrEmpty()) { "The 'dependenciesFilesPaths' property is required" }
    }

    fun validateHeadBranch() {
        requireNotNull(headBranch) { "The 'headBranch' property is required" }
    }

    fun validateBaseBranch() {
        requireNotNull(baseBranch) { "The 'baseBranch' property is required" }
    }

    fun validateCommitMessage() {
        requireNotNull(commitMessage) { "The 'commitMessage' property is required" }
    }

    fun validatePullRequestTitle() {
        requireNotNull(pullRequestTitle) { "The 'pullRequestTitle' property is required" }
    }

    fun validateGitHubRepositoryOwner() {
        requireNotNull(gitHubRepositoryOwner) { "The 'gitHubRepositoryOwner' property is required" }
    }

    fun validateGitHubRepositoryName() {
        requireNotNull(gitHubRepositoryName) { "The 'gitHubRepositoryName' property is required" }
    }

    fun validateGitHubWriteToken() {
        requireNotNull(gitHubWriteToken) { "The 'gitHubWriteToken' property is required" }
    }
}
