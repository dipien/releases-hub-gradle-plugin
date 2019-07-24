package com.releaseshub.gradle.plugin.artifacts

import com.releaseshub.gradle.plugin.task.Dependency

object ArtifactsService {

    fun getArtifactsToUpdate(dependencies: List<Dependency>): List<Artifact> {
        // TODO
        return dependencies.map {
            val artifact = Artifact()
            artifact.groupId = it.groupId
            artifact.artifactId = it.artifactId
            artifact.previousVersion = it.version
            artifact.latestVersion = "9.9.9"
            return listOf(artifact)
        }
    }

}