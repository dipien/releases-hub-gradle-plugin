package com.releaseshub.gradle.plugin.common

import com.releaseshub.gradle.plugin.ReleasesHubGradlePlugin
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension
import com.releaseshub.gradle.plugin.artifacts.MavenArtifactRepository
import com.releaseshub.gradle.plugin.artifacts.api.AppServer
import com.releaseshub.gradle.plugin.artifacts.api.AppService
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AbstractTask : DefaultTask() {

    companion object {
        val DEPENDENCIES_BASE_PATH = "buildSrc" + File.separator + "src" + File.separator + "main" + File.separator + "kotlin" + File.separator
    }

    var logLevel: LogLevel? = null
    protected lateinit var propertyResolver: PropertyResolver
    protected lateinit var commandExecutor: CommandExecutor
    protected lateinit var gitHelper: GitHelper

    var dependenciesClassNames: List<String>? = null
    lateinit var includes: List<String>
    lateinit var excludes: List<String>
    var serverName: String? = null
    var userToken: String? = null

    @TaskAction
    fun doExecute() {
        propertyResolver = PropertyResolver(project)
        commandExecutor = CommandExecutor(project, logLevel)
        gitHelper = GitHelper(commandExecutor)
        onExecute()
    }

    protected fun getExtension(): ReleasesHubGradlePluginExtension {
        return project.extensions.getByName(ReleasesHubGradlePlugin.EXTENSION_NAME) as ReleasesHubGradlePluginExtension
    }

    protected fun log(message: String) {
        logger.log(logLevel, message)
    }

    protected fun createArtifactsService(): AppService {
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
