package com.releaseshub.gradle.plugin.artifacts

import com.releaseshub.gradle.plugin.artifacts.api.AppService
import com.releaseshub.gradle.plugin.artifacts.fetch.ArtifactUpgradeHelper
import com.releaseshub.gradle.plugin.artifacts.fetch.MavenArtifactRepository

class ArtifactsService(private val apiService: AppService) {

    fun getArtifactsToUpgrade(artifactsToCheck: List<ArtifactUpgrade>, repositories: List<MavenArtifactRepository>): List<ArtifactUpgrade> {
        val artifactsUpgrades = apiService.getArtifactsToUpgrade(artifactsToCheck).toMutableList()

        val artifactsToCheckLocally = artifactsToCheck.toMutableList()
        artifactsToCheckLocally.removeAll(artifactsUpgrades)

        artifactsUpgrades.addAll(ArtifactUpgradeHelper.getArtifactsToUpgrade(artifactsToCheckLocally, repositories))
        return artifactsUpgrades.filter { it.fromVersion != it.toVersion }
    }
}