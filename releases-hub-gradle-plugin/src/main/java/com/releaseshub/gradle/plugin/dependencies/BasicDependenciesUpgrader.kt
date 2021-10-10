package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.common.LoggerHelper
import com.releaseshub.gradle.plugin.task.UpgradeResult
import java.io.File

class BasicDependenciesUpgrader : DependenciesUpgrader {

    override fun upgradeDependenciesFile(dependenciesFile: File, artifactToUpgrade: ArtifactUpgrade): UpgradeResult? {
        var upgradedUpgradeResult: UpgradeResult? = null
        val lines = dependenciesFile.readLines()
        val newLines = mutableListOf<String>()
        dependenciesFile.bufferedWriter().use { out ->
            lines.forEach { line ->
                val upgradeResult = upgradeDependency(line, artifactToUpgrade)
                if (upgradeResult.upgraded) {
                    LoggerHelper.log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                    upgradedUpgradeResult = upgradeResult
                }
                newLines.add(upgradeResult.line)
                out.write(upgradeResult.line + "\n")
            }
        }
        return upgradedUpgradeResult
    }

    private fun upgradeDependency(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        var matchResult = DependenciesExtractor.getDependencyMatchResult(line)
        if (matchResult != null) {
            if (artifactToUpgrade.groupId == matchResult.groupValues[1] && artifactToUpgrade.artifactId == matchResult.groupValues[2]) {
                val newLine = line.replaceFirst(matchResult.groupValues[3], artifactToUpgrade.toVersion!!)
                if (newLine != line) {
                    artifactToUpgrade.fromVersion = matchResult.groupValues[3]
                    return UpgradeResult(true, artifactToUpgrade, newLine)
                }
            }
        } else if (artifactToUpgrade.groupId == "com.gradle" && artifactToUpgrade.artifactId == "gradle-enterprise-gradle-plugin") {
            matchResult = DependenciesExtractor.getPluginsMatchResult(line)
            if (matchResult != null) {
                val pluginId = matchResult.groupValues[1]
                // TODO Find a way to automatically map all the plugin ids to a groupId:artifactId
                if (pluginId == "com.gradle.enterprise") {
                    val newLine = line.replaceFirst(matchResult.groupValues[2], artifactToUpgrade.toVersion!!)
                    if (newLine != line) {
                        artifactToUpgrade.fromVersion = matchResult.groupValues[2]
                        return UpgradeResult(true, artifactToUpgrade, newLine)
                    }
                }
            }
        }
        return UpgradeResult(false, null, line)
    }
}
