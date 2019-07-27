package com.releaseshub.gradle.plugin.artifacts

import com.releaseshub.gradle.plugin.artifacts.api.AppService

object ArtifactsService {

    fun getArtifactsToUpgrade(artifacts: List<Artifact>): List<Artifact> {
        val appService = AppService()
        val artifactsToUpgrade = appService.getArtifactsTooUpgrade(artifacts)
        return artifactsToUpgrade.onEach { artifactToUpgrade ->
            artifactToUpgrade.previousVersion = artifacts.find {
                it.groupId == artifactToUpgrade.groupId && it.artifactId == artifactToUpgrade.artifactId
            }!!.previousVersion
        }
    }

}