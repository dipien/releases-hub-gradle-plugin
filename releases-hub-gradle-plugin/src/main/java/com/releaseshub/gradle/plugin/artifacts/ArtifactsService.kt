package com.releaseshub.gradle.plugin.artifacts


object ArtifactsService {

    fun getArtifactsToUpdate(artifacts: List<Artifact>): List<Artifact> {
        // TODO
        return artifacts.onEach {
            it.latestVersion = "9.9.9"
        }
    }

}