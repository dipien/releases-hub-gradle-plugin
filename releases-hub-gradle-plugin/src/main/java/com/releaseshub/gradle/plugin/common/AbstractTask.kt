package com.releaseshub.gradle.plugin.common

import com.releaseshub.gradle.plugin.ReleasesHubGradlePlugin
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension
import com.releaseshub.gradle.plugin.artifacts.ArtifactsService
import com.releaseshub.gradle.plugin.artifacts.api.AppServer
import com.releaseshub.gradle.plugin.artifacts.api.AppService
import com.releaseshub.gradle.plugin.artifacts.fetch.MavenArtifactRepository
import com.releaseshub.gradle.plugin.dependencies.GradleHelper
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class AbstractTask : DefaultTask() {

    @get:Input
    var logLevel: LogLevel? = null

    @get:Internal
    protected lateinit var commandExecutor: CommandExecutor

    @get:Internal
    protected lateinit var gitHelper: GitHelper

    @get:Input
    var autoDetectDependenciesPaths: Boolean = true

    @get:Input
    @get:Optional
    var dependenciesPaths: List<String>? = null

    @get:Input
    @get:Optional
    var includes: List<String>? = null

    @get:Input
    @get:Optional
    var excludes: List<String>? = null

    @get:Input
    var serverName: String? = null

    @get:Input
    var userToken: String? = null

    init {
        group = "Releases Hub"
    }

    @TaskAction
    fun doExecute() {

        LoggerHelper.logger = logger
        LoggerHelper.logLevel = logLevel!!

        commandExecutor = CommandExecutorImpl(logger, logLevel!!)
        gitHelper = GitHelper(commandExecutor)
        onExecute()
    }

    @Internal
    protected fun getExtension(): ReleasesHubGradlePluginExtension {
        return project.extensions.getByName(ReleasesHubGradlePlugin.EXTENSION_NAME) as ReleasesHubGradlePluginExtension
    }

    @Internal
    protected fun getAllDependenciesPaths(): List<String> {
        val paths = dependenciesPaths.orEmpty().filter { File(project.rootDir, it).exists() }.toMutableList()
        if (autoDetectDependenciesPaths) {
            val buildSrcBasePath = "buildSrc${File.separator}src${File.separator}main${File.separator}kotlin${File.separator}"
            val defaultPaths = listOf(
                "${buildSrcBasePath}Libs.kt",
                "${buildSrcBasePath}BuildLibs.kt",
                "gradle${File.separator}libs.versions.toml",
                "settings.gradle.kts",
                "settings.gradle",
                GradleHelper.GRADLE_WRAPPER_PROPERTIES_RELATIVE_PATH
            ).filter { File(project.rootDir, it).exists() }
            paths.addAll(defaultPaths)
            project.rootProject.allprojects {
                paths.add(it.buildFile.absolutePath.replaceFirst(project.rootDir.absolutePath + File.separator, ""))
            }
        }
        return paths.toSet().toList()
    }

    protected fun log(message: String) {
        LoggerHelper.log(message)
    }

    protected fun createArtifactsService(): ArtifactsService {
        return ArtifactsService(createAppService())
    }

    private fun createAppService(): AppService {
        return AppService(AppServer.valueOf(serverName!!), userToken!!)
    }

    @Internal
    protected fun getRepositories(): List<MavenArtifactRepository> {
        val repositories = mutableListOf<MavenArtifactRepository>()
        project.repositories.plus(project.buildscript.repositories).forEach {
            if (it is org.gradle.api.artifacts.repositories.MavenArtifactRepository) {
                if (it.url.scheme == "http" || it.url.scheme == "https") {
                    val url = it.url.toString().dropLastWhile { char -> char == '/' }
                    repositories.add(MavenArtifactRepository(it.name, url))
                }
            }
        }
        return repositories.distinctBy { it.url }
    }

    protected abstract fun onExecute()
}
