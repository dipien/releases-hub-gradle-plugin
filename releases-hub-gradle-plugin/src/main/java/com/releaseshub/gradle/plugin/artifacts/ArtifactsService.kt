package com.releaseshub.gradle.plugin.artifacts

import com.jdroid.java.http.Server
import com.releaseshub.gradle.plugin.artifacts.api.AppService

class ArtifactsService(private val server: Server) {

    fun getArtifactsToUpgrade(artifacts: List<Artifact>): List<Artifact> {
        return AppService(server).getArtifactsToUpgrade(artifacts)
    }
}