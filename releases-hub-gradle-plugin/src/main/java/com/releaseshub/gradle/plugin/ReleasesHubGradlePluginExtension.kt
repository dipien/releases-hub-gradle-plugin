package com.releaseshub.gradle.plugin

import com.releaseshub.gradle.plugin.common.PropertyResolver

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

open class ReleasesHubGradlePluginExtension(project: Project) {

    private val propertyResolver: PropertyResolver = PropertyResolver(project)

    var dependenciesFilesPaths = mutableListOf<String>()
    var includes = mutableListOf<String>()
    var excludes = mutableListOf<String>()

    var headBranch = "dependencies_upgrade"
    var baseBranch = "master"
    var commitMessage = "Upgraded dependencies"
    var pullRequestTitle = "Upgraded dependencies"
    var pullRequestEnabled = false

    var gitHubUserName: String? = null
    var gitHubUserEmail: String? = null
    lateinit var gitHubRepositoryOwner: String
    lateinit var gitHubRepositoryName: String

    var logLevel = LogLevel.LIFECYCLE
}
