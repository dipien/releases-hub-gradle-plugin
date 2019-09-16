package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.artifacts.api.AppServer
import com.releaseshub.gradle.plugin.artifacts.api.HeadersAppender
import com.releaseshub.gradle.plugin.common.PropertyResolver

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.File

open class ReleasesHubGradlePluginExtension(project: Project) {

    private val propertyResolver: PropertyResolver = PropertyResolver(project)

    var serverName: String?
    var userToken: String?

    var dependenciesBasePath: String
    var dependenciesClassNames: List<String>?
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
        serverName = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::serverName.name, AppServer.PROD.getServerName())
        userToken = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::userToken.name, HeadersAppender.DEFAULT_USER_TOKEN_HEADER)

        dependenciesBasePath = "buildSrc" + File.separator + "src" + File.separator + "main" + File.separator + "kotlin" + File.separator
        dependenciesClassNames = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::dependenciesClassNames.name, listOf("Libs.kt", "BuildLibs.kt"))
        includes = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::includes.name, listOf()) ?: listOf()
        excludes = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::excludes.name, listOf()) ?: listOf()

        headBranch = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::headBranch.name, "dependencies_upgrade")
        baseBranch = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::baseBranch.name, "master")
        commitMessage = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::commitMessage.name, "Upgraded dependencies")
        pullRequestTitle = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::pullRequestTitle.name, commitMessage)
        pullRequestEnabled = propertyResolver.getBooleanProp(ReleasesHubGradlePluginExtension::pullRequestEnabled.name, false) ?: false

        gitHubUserName = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubUserName.name)
        gitHubUserEmail = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubUserEmail.name)

        gitHubRepositoryOwner = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubRepositoryOwner.name)
        gitHubRepositoryName = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubRepositoryName.name)
        gitHubWriteToken = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::gitHubWriteToken.name)
    }

    fun validateServerName() {
        requireNotNull(serverName.isNullOrEmpty()) { "The 'serverName' property is required" }
    }

    fun validateUserToken() {
        requireNotNull(userToken.isNullOrEmpty()) { "The 'userToken' property is required" }
    }

    fun validateDependenciesClassNames() {
        require(!dependenciesClassNames.isNullOrEmpty()) { "The 'dependenciesClassNames' property is required" }
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
