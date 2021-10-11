package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.artifacts.api.AppServer
import com.releaseshub.gradle.plugin.artifacts.api.HeadersAppender
import com.releaseshub.gradle.plugin.common.propertyResolver
import java.io.File
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

open class ReleasesHubGradlePluginExtension(project: Project) {

    var server: String? = project.propertyResolver.getStringProp(::server.name, AppServer.PROD.getServerName())
    var userToken: String? = project.propertyResolver.getStringProp(::userToken.name, HeadersAppender.DEFAULT_USER_TOKEN_HEADER)

    var autoDetectDependenciesPaths: Boolean = project.propertyResolver.getBooleanProp(::autoDetectDependenciesPaths.name, true) ?: true

    var dependenciesPaths: List<String> = project.propertyResolver.getRequiredStringListProp(::dependenciesPaths.name, listOf())

    var includes: List<String>? = project.propertyResolver.getStringListProp(::includes.name)
    var excludes: List<String>? = project.propertyResolver.getStringListProp(::excludes.name)

    var unusedExcludes: List<String> = project.propertyResolver.getRequiredStringListProp(::unusedExcludes.name, emptyList())
    var unusedExtensionsToSearch: List<String> = project.propertyResolver.getRequiredStringListProp(::unusedExtensionsToSearch.name, listOf(".kt", ".java", ".xml"))

    var baseBranch: String? = project.propertyResolver.getStringProp(::baseBranch.name, "master")

    var headBranchPrefix: String? = project.propertyResolver.getStringProp(::headBranchPrefix.name, "releases_hub/")

    var pullRequestEnabled: Boolean = project.propertyResolver.getBooleanProp(::pullRequestEnabled.name, true) ?: true
    var pullRequestsMax: Int = project.propertyResolver.getIntegerProp(::pullRequestsMax.name, 5) ?: 5
    var pullRequestLabels: List<String>? = project.propertyResolver.getStringListProp(::pullRequestLabels.name)
    var pullRequestAssignee: String? = project.propertyResolver.getStringProp(::pullRequestAssignee.name)
    var pullRequestReviewers: List<String>? = project.propertyResolver.getStringListProp(::pullRequestReviewers.name)
    var pullRequestTeamReviewers: List<String>? = project.propertyResolver.getStringListProp(::pullRequestTeamReviewers.name)

    var gitUserName: String? = project.propertyResolver.getStringProp(::gitUserName.name)
    var gitUserEmail: String? = project.propertyResolver.getStringProp(::gitUserEmail.name)

    var gitHubRepository: String? = project.propertyResolver.getStringProp(::gitHubRepository.name)
    var gitHubRepositoryOwner: String? = project.propertyResolver.getStringProp(::gitHubRepositoryOwner.name)
    var gitHubRepositoryName: String? = project.propertyResolver.getStringProp(::gitHubRepositoryName.name)
    var gitHubWriteToken: String? = project.propertyResolver.getStringProp(::gitHubWriteToken.name)
    var gitHubApiHostName: String? = project.propertyResolver.getStringProp(::gitHubApiHostName.name)

    var logLevel = LogLevel.LIFECYCLE

    fun validateServerName() {
        requireNotNull(server.isNullOrEmpty()) { "The '${::server.name}' property is required" }
    }

    fun validateUserToken() {
        requireNotNull(userToken.isNullOrEmpty()) { "The '${::userToken.name}' property is required" }
    }

    fun validateDependenciesPaths() {
        require(!dependenciesPaths.isNullOrEmpty()) { "The '${::dependenciesPaths.name}' property is required" }
    }

    fun validateBaseBranch() {
        requireNotNull(baseBranch) { "The '${::baseBranch.name}' property is required" }
    }

    fun validateHeadBranchPrefix() {
        requireNotNull(headBranchPrefix) { "The '${::headBranchPrefix.name}' property is required" }
    }

    fun validateGitHubRepositoryOwner() {
        requireNotNull(gitHubRepositoryOwner) { "The '${::gitHubRepositoryOwner.name}' property is required" }
    }

    fun validateGitHubRepositoryName() {
        requireNotNull(gitHubRepositoryName) { "The '${::gitHubRepositoryName.name}' property is required" }
    }

    fun validateGitHubWriteToken() {
        requireNotNull(gitHubWriteToken) { "The '${::gitHubWriteToken.name}' property is required" }
    }
}
