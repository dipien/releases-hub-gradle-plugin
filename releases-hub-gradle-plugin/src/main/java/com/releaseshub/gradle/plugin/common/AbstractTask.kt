package com.releaseshub.gradle.plugin.common

import com.releaseshub.gradle.plugin.ReleasesHubGradlePlugin
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension

import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction

abstract class AbstractTask : DefaultTask() {

    var logLevel: LogLevel? = null
    protected lateinit var propertyResolver: PropertyResolver
    protected lateinit var commandExecutor: CommandExecutor

    @TaskAction
    fun doExecute() {
        propertyResolver = PropertyResolver(project)
        commandExecutor = CommandExecutor(project, logLevel)
        onExecute()
    }

    protected fun getExtension(): ReleasesHubGradlePluginExtension {
        return project.extensions.getByName(ReleasesHubGradlePlugin.EXTENSION_NAME) as ReleasesHubGradlePluginExtension
    }

    protected fun log(message: String) {
        logger.log(logLevel, message)
    }

    protected abstract fun onExecute()
}
