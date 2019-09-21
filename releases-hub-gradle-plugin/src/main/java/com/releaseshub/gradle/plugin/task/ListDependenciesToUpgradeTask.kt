package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesToUpgradeTask : AbstractTask() {

    init {
        description = "List all dependencies to upgrade"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
        getExtension().validateDependenciesClassNames()

        val artifacts = mutableListOf<ArtifactUpgrade>()
        val excludedArtifacts = mutableListOf<ArtifactUpgrade>()
        dependenciesClassNames!!.forEach {
            project.rootProject.file(dependenciesBasePath + it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null) {
                    if (artifact.match(includes, excludes)) {
                        artifacts.add(artifact)
                    } else {
                        excludedArtifacts.add(artifact)
                    }
                }
            }
        }

        if (excludedArtifacts.isNotEmpty()) {
            log("Dependencies excluded:")
            excludedArtifacts.forEach {
                log(" * $it ${it.fromVersion}")
            }
            log("")
        }

        val artifactsUpgrades = createArtifactsService().getArtifactsUpgrades(artifacts, getRepositories())

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
                log("")
            }
        }
    }
}
