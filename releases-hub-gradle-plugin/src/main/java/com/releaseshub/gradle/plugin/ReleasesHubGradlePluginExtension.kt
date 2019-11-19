package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.artifacts.api.AppServer
import com.releaseshub.gradle.plugin.artifacts.api.HeadersAppender
import com.releaseshub.gradle.plugin.common.PropertyResolver

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.File

open class ReleasesHubGradlePluginExtension(project: Project) {

    private val propertyResolver: PropertyResolver = PropertyResolver(project)

    var serverName: String? = propertyResolver.getStringProp(::serverName.name, AppServer.PROD.getServerName())
    var userToken: String? = propertyResolver.getStringProp(::userToken.name, HeadersAppender.DEFAULT_USER_TOKEN_HEADER)

    var dependenciesBasePath: String = "buildSrc" + File.separator + "src" + File.separator + "main" + File.separator + "kotlin" + File.separator
    var dependenciesClassNames: List<String>? = propertyResolver.getStringListProp(::dependenciesClassNames.name, listOf("Libs.kt", "BuildLibs.kt"))
    var includes: List<String> = propertyResolver.getStringListProp(::includes.name, emptyList()) ?: emptyList()
    var excludes: List<String> = propertyResolver.getStringListProp(::excludes.name, emptyList()) ?: emptyList()

    var unusedExcludes: List<String> = propertyResolver.getStringListProp(::unusedExcludes.name, emptyList()) ?: emptyList()

    var baseBranch: String? = propertyResolver.getStringProp(::baseBranch.name, "master")

    @Deprecated(message = "Use headBranchPrefix instead. To be removed on v2.0.0")
    var headBranch: String? = null

    var headBranchPrefix: String? = propertyResolver.getStringProp(::headBranchPrefix.name, "releases_hub/")

    @Deprecated(message = "Not used anymore, because we create a commit per update. To be removed on v2.0.0")
    var commitMessage: String? = null

    @Deprecated(message = "Not used anymore, because we create a pull request per group id. To be removed on v2.0.0")
    var pullRequestTitle: String? = null

    var pullRequestEnabled: Boolean = propertyResolver.getBooleanProp(::pullRequestEnabled.name, false) ?: false
    var pullRequestsMax: Int = propertyResolver.getIntegerProp(::pullRequestsMax.name, 5) ?: 5
    var pullRequestLabels: List<String>? = propertyResolver.getStringListProp(::pullRequestLabels.name)
    var pullRequestReviewers: List<String>? = propertyResolver.getStringListProp(::pullRequestReviewers.name)
    var pullRequestTeamReviewers: List<String>? = propertyResolver.getStringListProp(::pullRequestTeamReviewers.name)

    var gitHubUserName: String? = propertyResolver.getStringProp(::gitHubUserName.name)
    var gitHubUserEmail: String? = propertyResolver.getStringProp(::gitHubUserEmail.name)
    var gitHubRepositoryOwner: String? = propertyResolver.getStringProp(::gitHubRepositoryOwner.name)
    var gitHubRepositoryName: String? = propertyResolver.getStringProp(::gitHubRepositoryName.name)
    var gitHubWriteToken: String? = propertyResolver.getStringProp(::gitHubWriteToken.name)

    var logLevel = LogLevel.LIFECYCLE

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
