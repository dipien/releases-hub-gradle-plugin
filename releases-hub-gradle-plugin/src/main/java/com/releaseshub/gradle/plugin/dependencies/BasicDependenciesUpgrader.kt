package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.common.CommandExecutor
import com.releaseshub.gradle.plugin.common.LoggerHelper
import com.releaseshub.gradle.plugin.task.UpgradeResult
import java.io.File
import java.lang.Exception

class BasicDependenciesUpgrader(private val commandExecutor: CommandExecutor) : DependenciesUpgrader {

    override fun upgradeDependenciesFile(rootDir: File, dependenciesFile: File, artifactToUpgrade: ArtifactUpgrade): UpgradeResult? {
        if (dependenciesFile.absolutePath == GradleHelper.getGradleWrapperPropertiesFile(rootDir).absolutePath) {
            if (artifactToUpgrade.id == ArtifactUpgrade.GRADLE_ID) {
                val upgradeResult = upgradeGradle(commandExecutor, rootDir, artifactToUpgrade)
                LoggerHelper.log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                return upgradeResult
            } else {
                return null
            }
        } else {
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
        } else {
            matchResult = DependenciesExtractor.getPluginsDSLMatchResult(line)
            if (matchResult != null) {
                if (artifactToUpgrade.matchPlugin(matchResult.groupValues[1])) {
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

    private fun upgradeGradle(commandExecutor: CommandExecutor, rootDir: File, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
        val gradleWrapperPropertiesContent = GradleHelper.getGradleWrapperPropertiesFile(rootDir).readText()
        val gradlewBatFile = GradleHelper.getGradleBatWrapperFile(rootDir)
        val keepGradleBatFile = gradlewBatFile.exists()

        // Note that running the wrapper task once will update gradle-wrapper.properties only, but leave the wrapper itself in
        // gradle-wrapper.jar untouched. This is usually fine as new versions of Gradle can be run even with ancient wrapper files.
        // If you nevertheless want all the wrapper files to be completely up-to-date, you’ll need to run the wrapper task a second time.
        val upgradeCommand = listOf("./gradlew", "wrapper", "--gradle-version=${artifactToUpgrade.toVersion!!}", "--stacktrace")
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
