package com.releaseshub.gradle.plugin.artifacts

import com.releaseshub.gradle.plugin.artifacts.api.AppService

object ArtifactsService {

    fun getArtifactsToUpgrade(artifacts: List<Artifact>): List<Artifact> {
        return AppService().getArtifactsToUpgrade(artifacts)
    }
}