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

    var baseBranch: String?

    @Deprecated(message = "Use headBranchPrefix instead. To be removed on v2.0.0")
    var headBranch: String? = null

    var headBranchPrefix: String?

    @Deprecated(message = "Not used anymore, because we create a commit per update. To be removed on v2.0.0")
    var commitMessage: String? = null

    @Deprecated(message = "Not used anymore, because we create a pull request per group id. To be removed on v2.0.0")
    var pullRequestTitle: String? = null

    var pullRequestEnabled: Boolean
    var pullRequestsMax: Int
    var pullRequestLabels: List<String>? = null
    var pullRequestReviewers: List<String>? = null
    var pullRequestTeamReviewers: List<String>? = null

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

        baseBranch = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::baseBranch.name, "master")
        headBranchPrefix = propertyResolver.getStringProp(ReleasesHubGradlePluginExtension::headBranchPrefix.name, "releases_hub/")
        pullRequestEnabled = propertyResolver.getBooleanProp(ReleasesHubGradlePluginExtension::pullRequestEnabled.name, false) ?: false
        pullRequestsMax = propertyResolver.getIntegerProp(ReleasesHubGradlePluginExtension::pullRequestsMax.name, 5) ?: 5
        pullRequestLabels = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::pullRequestLabels.name)
        pullRequestReviewers = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::pullRequestReviewers.name)
        pullRequestTeamReviewers = propertyResolver.getStringListProp(ReleasesHubGradlePluginExtension::pullRequestTeamReviewers.name)

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

    fun validateBaseBranch() {
        requireNotNull(baseBranch) { "The 'baseBranch' property is required" }
    }

    fun validateHeadBranchPrefix() {
        requireNotNull(headBranchPrefix) { "The 'headBranchPrefix' property is required" }
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
