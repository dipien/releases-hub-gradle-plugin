package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade

class DependenciesExtractorResult {

    val artifactsMap = mutableMapOf<String, List<ArtifactUpgrade>>()
    val excludedArtifacts = mutableListOf<ArtifactUpgrade>()
    val dependenciesLinesMap = mutableMapOf<String, List<String>>()

    fun getAllArtifacts(): List<ArtifactUpgrade> {
        val artifacts = mutableListOf<ArtifactUpgrade>()
        artifactsMap.values.forEach { artifacts.addAll(it) }
        return artifacts.sortedBy { it.toString() }
    }
}
