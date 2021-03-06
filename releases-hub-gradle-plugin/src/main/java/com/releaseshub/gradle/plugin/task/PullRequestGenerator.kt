package com.releaseshub.gradle.plugin.task

import com.jdroid.java.date.DateTimeFormat
import com.jdroid.java.date.DateUtils
import com.releaseshub.gradle.plugin.core.FileSizeFormatter
import java.util.Date

object PullRequestGenerator {

    fun createBody(upgradeResults: List<UpgradeResult>, pluginVersion: String): String {
        val builder = StringBuilder()
        builder.appendln("## Dependencies upgrades")
        addCommonText(builder, upgradeResults)
        builder.appendln()
        builder.append("---")
        builder.appendln()
        builder.append("This pull request was automatically generated by **[Releases Hub Gradle Plugin v$pluginVersion](https://github.com/releaseshub/releases-hub-gradle-plugin)**")
        return builder.toString()
    }

    private fun addCommonText(builder: StringBuilder, upgradeResults: List<UpgradeResult>) {
        upgradeResults.forEach {
            builder.appendln("### ${it.artifactUpgrade}")
            var atLeastOneItem = false
            builder.appendln("* **Version:** `${it.artifactUpgrade?.fromVersion}` -> `${it.artifactUpgrade?.toVersion}`")
            if (it.artifactUpgrade?.toSize != null) {
                builder.appendln("* **Size:** `${FileSizeFormatter.format(it.artifactUpgrade.toSize!!)}`")
            }
            if (it.artifactUpgrade?.toReleaseDate != null) {
                builder.appendln("* **Release Date:** `${DateUtils.format(Date(it.artifactUpgrade.toReleaseDate!!), DateTimeFormat.MMMDYYYY)}`")
            }
            if (!it.artifactUpgrade?.toAndroidPermissions.isNullOrEmpty()) {
                builder.appendln("* **Android permissions:** `${it.artifactUpgrade?.toAndroidPermissions}`")
            }
            if (it.artifactUpgrade?.releaseNotesUrl != null) {
                builder.append("* [Releases notes](${it.artifactUpgrade.releaseNotesUrl})")
                atLeastOneItem = true
            }
            if (it.artifactUpgrade?.sourceCodeUrl != null) {
                builder.append(if (atLeastOneItem) " | " else "* ")
                builder.append("[Source code](${it.artifactUpgrade.sourceCodeUrl})")
                atLeastOneItem = true
            }
            if (it.artifactUpgrade?.documentationUrl != null) {
                builder.append(if (atLeastOneItem) " | " else "* ")
                builder.append("[Documentation](${it.artifactUpgrade.documentationUrl})")
                atLeastOneItem = true
            }
            if (it.artifactUpgrade?.issueTrackerUrl != null) {
                builder.append(if (atLeastOneItem) " | " else "* ")
                builder.append("[Issue tracker](${it.artifactUpgrade.issueTrackerUrl})")
                atLeastOneItem = true
            }
            if (atLeastOneItem) {
                builder.appendln()
            }
        }
    }

    fun createComment(upgradeResults: List<UpgradeResult>): String {
        val builder = StringBuilder()
        addCommonText(builder, upgradeResults)
        return builder.toString()
    }
}
