package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesToUpgradeTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "listDependenciesToUpgrade"
    }

    init {
        description = "List all dependencies to upgrade"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
        getExtension().validateDependenciesClassNames()

        val dependenciesParserResult = DependenciesParser.extractArtifacts(project, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes)

        if (dependenciesParserResult.excludedArtifacts.isNotEmpty()) {
            log("Dependencies excluded:")
            dependenciesParserResult.excludedArtifacts.sortedBy { it.toString() }.forEach {
                log(" * $it ${it.fromVersion}")
            }
            log("")
        }

        val artifactsUpgrades = createArtifactsService().getArtifactsUpgrades(dependenciesParserResult.getAllArtifacts(), getRepositories())

        val notFoundArtifacts = artifactsUpgrades.filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.NOT_FOUND }
        if (notFoundArtifacts.isNotEmpty()) {
            log("Dependencies not found:")
            notFoundArtifacts.forEach {
                log(" * $it ${it.fromVersion}")
            }
            log("")
        }

        val artifactsToUpgrade = artifactsUpgrades.filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.PENDING_UPGRADE }
        if (artifactsToUpgrade.isNullOrEmpty()) {
            log("No dependencies to upgrade")
        } else {
            log("Dependencies to upgrade:")
            artifactsToUpgrade.forEach {
                log(" * $it ${it.fromVersion} -> ${it.toVersion}")
                if (it.releaseNotesUrl != null) {
                    log("   - Releases notes: ${it.releaseNotesUrl}")
                }
                if (it.sourceCodeUrl != null) {
                    log("   - Source code: ${it.sourceCodeUrl}")
                }
                if (it.documentationUrl != null) {
                    log("   - Documentation: ${it.documentationUrl}")
                }
                if (it.issueTrackerUrl != null) {
                    log("   - Issue tracker: ${it.issueTrackerUrl}")
                }
                log("")
            }
        }
    }
}
