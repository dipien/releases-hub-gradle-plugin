package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.common.CommandExecutor
import com.releaseshub.gradle.plugin.common.LoggerHelper
import com.releaseshub.gradle.plugin.task.UpgradeResult
import java.io.File
import java.lang.Exception

object BuildSrcDependenciesUpgrader {

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

        // Note that running the wrapper task once will update gradle-wrapper.properties only, but leave the wrapper itself in 
        // gradle-wrapper.jar untouched. This is usually fine as new versions of Gradle can be run even with ancient wrapper files. 
        // If you nevertheless want all the wrapper files to be completely up-to-date, you’ll need to run the wrapper task a second time.
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
