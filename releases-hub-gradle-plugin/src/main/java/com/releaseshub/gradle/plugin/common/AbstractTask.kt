package com.releaseshub.gradle.plugin.common

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

    @get:Internal
    lateinit var rootProjectDir: File

    @get:Internal
    lateinit var rootProjectBuildDir: File

    @get:Internal
    lateinit var repositories: List<MavenArtifactRepository>

    @get:Internal
    var buildFileAbsolutePaths: MutableList<String> = mutableListOf()

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

        println()
        println("***********************************************************")
        println("* You can support this project, so we can continue improving it:")
        println("* - Donate with Bitcoin Lightning: http://alby.dipien.com/")
        println("* - Donate cryptocurrency: http://coinbase.dipien.com/")
        println("* - Donate with credit card: http://kofi.dipien.com/")
        println("* - Donate on Patreon: http://patreon.dipien.com/")
        println("* - Become a member of Medium (We will receive a portion of your membership fee): https://membership.medium.dipien.com")
        println("* Thanks !!!")
        println("***********************************************************")
    }

    @Internal
    protected fun getAllDependenciesPaths(): List<String> {
        val paths = dependenciesPaths.orEmpty().filter { File(rootProjectDir, it).exists() }.toMutableList()
        if (autoDetectDependenciesPaths) {
            val buildSrcBasePath = "buildSrc${File.separator}src${File.separator}main${File.separator}kotlin${File.separator}"
            val defaultPaths = listOf(
                "${buildSrcBasePath}Libs.kt",
                "${buildSrcBasePath}BuildLibs.kt",
                "gradle${File.separator}libs.versions.toml",
                "settings.gradle.kts",
                "settings.gradle",
                GradleHelper.GRADLE_WRAPPER_PROPERTIES_RELATIVE_PATH
            ).filter { File(rootProjectDir, it).exists() }
            paths.addAll(defaultPaths)
            buildFileAbsolutePaths.forEach {
                paths.add(it.replaceFirst(rootProjectDir.absolutePath + File.separator, ""))
            }
        }
        return paths.toSet().toList()
    }

    protected fun log(message: String) {
        LoggerHelper.log(message)
    }

    protected abstract fun onExecute()
}
