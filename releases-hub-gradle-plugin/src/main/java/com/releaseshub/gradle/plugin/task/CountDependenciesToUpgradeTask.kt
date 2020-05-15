package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask
import org.gradle.api.logging.LogLevel

open class CountDependenciesToUpgradeTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "countDependenciesToUpgrade"
    }

    init {
        description = "Count all dependencies to upgrade"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
        getExtension().validateDependenciesClassNames()

        val dependenciesParserResult = DependenciesExtractor.extractArtifacts(project.rootProject.projectDir, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes)
        val artifactsUpgrades = createArtifactsService().getArtifactsUpgrades(dependenciesParserResult.getAllArtifacts(), getRepositories())
        val artifactsToUpgrade = artifactsUpgrades.filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.PENDING_UPGRADE }

        logger.log(LogLevel.QUIET, artifactsToUpgrade.size.toString())
    }
}
