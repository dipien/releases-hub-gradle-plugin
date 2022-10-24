package com.releaseshub.gradle.plugin.artifacts

import com.releaseshub.gradle.plugin.artifacts.api.AppService
import com.releaseshub.gradle.plugin.artifacts.fetch.ArtifactUpgradeHelper
import com.releaseshub.gradle.plugin.artifacts.fetch.MavenArtifactRepository

class ArtifactsService(private val apiService: AppService) {

    fun getArtifactsUpgrades(artifactsToCheck: List<ArtifactUpgrade>, repositories: List<MavenArtifactRepository>, serverless: Boolean): List<ArtifactUpgrade> {
        val artifactsUpgrades = if (serverless) mutableListOf() else apiService.getArtifactsToUpgrade(artifactsToCheck).toMutableList()

        val artifactsToCheckLocally = artifactsToCheck.toMutableList()
        artifactsToCheckLocally.removeAll(artifactsUpgrades)

        artifactsUpgrades.addAll(ArtifactUpgradeHelper.getArtifactsUpgrades(artifactsToCheckLocally, repositories))

        return artifactsUpgrades.sortedBy { it.toReleaseDate }
    }
}
