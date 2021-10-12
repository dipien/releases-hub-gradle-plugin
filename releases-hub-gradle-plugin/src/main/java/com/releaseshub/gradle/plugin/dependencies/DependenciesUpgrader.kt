package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.task.UpgradeResult
import java.io.File

interface DependenciesUpgrader {

    fun upgradeDependenciesFile(rootDir: File, dependenciesFile: File, artifactToUpgrade: ArtifactUpgrade): UpgradeResult?
}
