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
        builder.append("This pull request was automatically generated by **[Releases Hub Gradle Plugin v$pluginVersion](https://github.com/dipien/releases-hub-gradle-plugin)**")
        return builder.toString()
    }

    private fun addCommonText(builder: StringBuilder, upgradeResults: List<UpgradeResult>) {
        upgradeResults.forEach {
            val artifactUpgrade = it.artifactUpgrade!!
            builder.appendln("### $artifactUpgrade")
            var atLeastOneItem = false

            if (artifactUpgrade.repository != null) {
                val baseUrl = "${artifactUpgrade.repository!!.url}/${artifactUpgrade.groupId!!.replace(".", "/")}/${artifactUpgrade.artifactId}"
                builder.appendln("* **Version:** [`${artifactUpgrade.fromVersion}`]($baseUrl/${artifactUpgrade.fromVersion}) -> [`${artifactUpgrade.toVersion}`]($baseUrl/${artifactUpgrade.toVersion})")
            } else {
                builder.appendln("* **Version:** `${artifactUpgrade.fromVersion}` -> `${artifactUpgrade.toVersion}`")
            }

            if (artifactUpgrade.toSize != null) {
                builder.appendln("* **Size:** `${FileSizeFormatter.format(artifactUpgrade.toSize!!)}`")
            }
            if (artifactUpgrade.toReleaseDate != null) {
                builder.appendln("* **Release Date:** `${DateUtils.format(Date(artifactUpgrade.toReleaseDate!!), DateTimeFormat.MMMDYYYY)}`")
            }
            if (!artifactUpgrade.toAndroidPermissions.isNullOrEmpty()) {
                builder.appendln("* **Android permissions:**")
                artifactUpgrade.toAndroidPermissions?.forEach { permission ->
                    // TODO Move the url logic to the backend
                    val url = permission.replace("android.permission.", "https://developer.android.com/reference/android/Manifest.permission#")
                    builder.appendln("  * [$permission]($url)")
                }
            }
            if (artifactUpgrade.releaseNotesUrl != null) {
                builder.append("* [Releases notes](${artifactUpgrade.releaseNotesUrl})")
                atLeastOneItem = true
            }
            if (artifactUpgrade.sourceCodeUrl != null) {
                builder.append(if (atLeastOneItem) " | " else "* ")
                builder.append("[Source code](${artifactUpgrade.sourceCodeUrl})")
                atLeastOneItem = true
            }
            if (artifactUpgrade.documentationUrl != null) {
                builder.append(if (atLeastOneItem) " | " else "* ")
                builder.append("[Documentation](${artifactUpgrade.documentationUrl})")
                atLeastOneItem = true
            }
            if (artifactUpgrade.issueTrackerUrl != null) {
                builder.append(if (atLeastOneItem) " | " else "* ")
                builder.append("[Issue tracker](${artifactUpgrade.issueTrackerUrl})")
                atLeastOneItem = true
            }
            if (!artifactUpgrade.detailsUrl.isNullOrEmpty()) {
                builder.appendln()
                builder.appendln("* [Official development resources](${artifactUpgrade.detailsUrl}): blog posts, Youtube videos, courses and trainings")
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
