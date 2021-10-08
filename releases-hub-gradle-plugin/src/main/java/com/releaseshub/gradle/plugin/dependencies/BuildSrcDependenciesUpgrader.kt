package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.task.UpgradeResult

class BuildSrcDependenciesUpgrader : DependenciesUpgrader {

    override fun upgradeDependency(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult {
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
}
