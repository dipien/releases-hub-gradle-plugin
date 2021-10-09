package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import java.io.File

class DependenciesExtractorResult {

    val artifactsMap = mutableMapOf<String, List<ArtifactUpgrade>>()
    val excludedArtifacts = mutableListOf<ArtifactUpgrade>()
    val dependenciesFiles = mutableListOf<File>()

    fun getAllArtifacts(): List<ArtifactUpgrade> {
        val artifacts = mutableListOf<ArtifactUpgrade>()
        artifactsMap.values.forEach { artifacts.addAll(it) }
        return artifacts.sortedBy { it.toString() }
    }
}
