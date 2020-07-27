package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File
import com.releaseshub.gradle.plugin.common.CommandExecutor
import com.releaseshub.gradle.plugin.common.LoggerHelper
import java.lang.Exception

object DependenciesUpgrader {

    fun upgradeDependency(artifactToUpgrade: ArtifactUpgrade, val upgradeResults: MutableList<UpgradeResult>) {
        var upgradedUpgradeResult: UpgradeResult? = null

        if (artifactToUpgrade.id == ArtifactUpgrade.GRADLE_ID) {
            val gradleWrapperFile = DependenciesExtractor.getGradleWrapperFile(project.rootProject.projectDir)
            if (gradleWrapperFile.exists()) {
                val lines = gradleWrapperFile.readLines()
                gradleWrapperFile.bufferedWriter().use { out ->
                    lines.forEach { line ->
                        val upgradeResult = DependenciesUpgrader.upgradeGradle(line, artifactToUpgrade)
                        if (upgradeResult.upgraded) {
                            upgradeResults.add(upgradeResult)
                            upgradedUpgradeResult = upgradeResult
                        }
                        out.write(upgradeResult.line)
                        out.newLine()
                    }
                }
            }
        } else {
            dependenciesLinesMapByGroup.entries.forEach { entry ->
                val newLines = mutableListOf<String>()
                File(entry.key).bufferedWriter().use { out ->
                    entry.value.forEach { line ->
                        val upgradeResult = DependenciesUpgrader.upgradeDependency(line, artifactToUpgrade)
                        if (upgradeResult.upgraded) {
                            upgradeResults.add(upgradeResult)
                            upgradedUpgradeResult = upgradeResult
                        }
                        newLines.add(upgradeResult.line)
                        out.write(upgradeResult.line + "\n")
                    }
                }
                dependenciesLinesMapByGroup[entry.key] = newLines
            }
        }
    }

    fun upgradeDependency(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        val matchResult = DependenciesExtractor.getDependencyMatchResult(line)
        if (matchResult != null) {
            if (artifactToUpgrade.groupId == matchResult.groupValues[1] && artifactToUpgrade.artifactId == matchResult.groupValues[2]) {
                val newLine = line.replaceFirst(matchResult.groupValues[3], artifactToUpgrade.toVersion!!)
                if (newLine != line) {
                    artifactToUpgrade.fromVersion = matchResult.groupValues[3]
                    return UpgradeResult(true, artifactToUpgrade, newLine)
                }
            }
        }
        return UpgradeResult(false, null, line)
    }

    fun upgradeGradle(commandExecutor: CommandExecutor, rootDir: File, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        val gradleWrapperPropertiesContent = GradleHelper.getGradleWrapperPropertiesFile(rootDir).readText()
        val gradlewBatFile = GradleHelper.getGradleBatWrapperFile(rootDir)
        val keepGradleBatFile = gradlewBatFile.exists()

        // We execute this twice because I had cases in the past where I had to do that to upgrade all files
        val upgradeCommand = "./gradlew wrapper --gradle-version=${artifactToUpgrade.toVersion!!} --stacktrace"
        try {
            commandExecutor.execute(upgradeCommand)
            commandExecutor.execute(upgradeCommand)

            if (!keepGradleBatFile) {
                gradlewBatFile.delete()
            }

            val newGradleWrapperPropertiesContent = GradleHelper.getGradleWrapperPropertiesFile(rootDir).readText()
            if (gradleWrapperPropertiesContent == newGradleWrapperPropertiesContent) {
                return UpgradeResult(false, null, "")
            } else {
                return UpgradeResult(true, artifactToUpgrade, "")
            }
        } catch (e: Exception) {
            LoggerHelper.log("Failed to upgrade gradle.", e)
            return UpgradeResult(false, null, "")
        }
    }
}
