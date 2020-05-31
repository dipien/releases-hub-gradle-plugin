package com.releaseshub.gradle.plugin.task

import com.jdroid.java.date.DateTimeFormat
import com.jdroid.java.date.DateUtils
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.core.FileSizeFormatter
import java.io.File
import java.util.Date

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

        val dependenciesParserResult = DependenciesExtractor.extractArtifacts(project.rootProject.projectDir, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes)

        if (dependenciesParserResult.excludedArtifacts.isNotEmpty()) {
            log("${dependenciesParserResult.excludedArtifacts.size} dependencies excluded:")
            dependenciesParserResult.excludedArtifacts.sortedBy { it.toString() }.forEach {
                log(" * $it ${it.fromVersion}")
            }
            log("")
        }

        val artifactsUpgrades = createArtifactsService().getArtifactsUpgrades(dependenciesParserResult.getAllArtifacts(), getRepositories())

        val notFoundArtifacts = artifactsUpgrades.filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.NOT_FOUND }
        if (notFoundArtifacts.isNotEmpty()) {
            log("${notFoundArtifacts.size} dependencies not found:")
            notFoundArtifacts.forEach {
                log(" * $it ${it.fromVersion}")
            }
            log("")
        }

        val artifactsToUpgrade = artifactsUpgrades.filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.PENDING_UPGRADE }
        if (artifactsToUpgrade.isNullOrEmpty()) {
            log("No dependencies to upgrade")
        } else {
            log("${artifactsToUpgrade.size} dependencies to upgrade:")
            artifactsToUpgrade.forEach {
                log(" * $it ${it.fromVersion} -> ${it.toVersion}")
                val releaseDate = it.toReleaseDate
                if (releaseDate != null) {
                    log("   - Release Date: " + DateUtils.format(Date(releaseDate), DateTimeFormat.MMMDYYYY))
                }
                if (it.toSize != null) {
                    val builder = StringBuilder()
                    builder.append("   - Size: ${FileSizeFormatter.format(it.toSize!!)}")
                    // TODO We need to store the complete history of versions on the backend to make the diff work
                    // log("**** fromSize " + it.fromSize)
                    // if (it.fromSize != null) {
                    //     val diff = it.toSize!! - it.fromSize!!
                    //     if (abs(diff) > 1024) {
                    //         builder.append("(" + (if (diff > 0) "+" else "-") + diff + " KB)")
                    //     }
                    // }
                    log(builder.toString())
                }
                if (!it.toAndroidPermissions.isNullOrEmpty()) {
                    log("   - Android permissions: ${it.toAndroidPermissions}")
                }
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

        val releasesHubDir = File(project.buildDir, File.separator + "releasesHub")
        releasesHubDir.mkdirs()
        val file = File(releasesHubDir, "dependencies_to_upgrade_count.txt")
        file.writeText(artifactsToUpgrade.size.toString())
    }
}
