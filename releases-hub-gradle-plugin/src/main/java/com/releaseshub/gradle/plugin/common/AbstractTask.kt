package com.releaseshub.gradle.plugin.common

import com.releaseshub.gradle.plugin.ReleasesHubGradlePlugin
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension
import com.releaseshub.gradle.plugin.artifacts.ArtifactsService
import com.releaseshub.gradle.plugin.artifacts.api.AppServer
import com.releaseshub.gradle.plugin.artifacts.api.AppService
import com.releaseshub.gradle.plugin.artifacts.fetch.MavenArtifactRepository
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction

abstract class AbstractTask : DefaultTask() {

    var logLevel: LogLevel? = null
    protected lateinit var propertyResolver: PropertyResolver
    protected lateinit var commandExecutor: CommandExecutor
    protected lateinit var gitHelper: GitHelper

    var dependenciesBasePath: String? = null
    var dependenciesClassNames: List<String>? = null
    lateinit var includes: List<String>
    lateinit var excludes: List<String>
    var serverName: String? = null
    var userToken: String? = null

    @TaskAction
    fun doExecute() {

        LoggerHelper.logger = logger
        LoggerHelper.logLevel = logLevel!!

        propertyResolver = PropertyResolver(project)
        commandExecutor = CommandExecutor(project, logLevel)
        gitHelper = GitHelper(commandExecutor)
        onExecute()
    }

    protected fun getExtension(): ReleasesHubGradlePluginExtension {
        return project.extensions.getByName(ReleasesHubGradlePlugin.EXTENSION_NAME) as ReleasesHubGradlePluginExtension
    }

    protected fun log(message: String) {
        LoggerHelper.log(message)
    }

    protected fun createArtifactsService(): ArtifactsService {
        return ArtifactsService(createAppService())
    }

    protected fun createAppService(): AppService {
        return AppService(AppServer.valueOf(serverName!!), project.version.toString(), userToken!!)
    }

    protected fun getRepositories(): List<MavenArtifactRepository> {
        val repositories = mutableListOf<MavenArtifactRepository>()
        project.repositories.plus(project.buildscript.repositories).forEach {
            if (it is org.gradle.api.artifacts.repositories.MavenArtifactRepository) {
                if (it.url.scheme == "http" || it.url.scheme == "https") {
                    repositories.add(MavenArtifactRepository(it.name, it.url.toString()))
                }
            }
        }
        return repositories.distinctBy { it.url }
    }

    protected abstract fun onExecute()
}
