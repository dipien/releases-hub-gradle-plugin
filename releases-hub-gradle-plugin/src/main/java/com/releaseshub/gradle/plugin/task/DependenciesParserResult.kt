package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade

class DependenciesParserResult {

    val artifactsMap = mutableMapOf<String, MutableList<ArtifactUpgrade>>()
    val excludedArtifacts = mutableListOf<ArtifactUpgrade>()
    val dependenciesLinesMap = mutableMapOf<String, List<String>>()

    fun getAllArtifacts(): List<ArtifactUpgrade> {
        val artifacts = mutableListOf<ArtifactUpgrade>()
        artifactsMap.values.forEach { artifacts.addAll(it) }
        return artifacts
    }

}