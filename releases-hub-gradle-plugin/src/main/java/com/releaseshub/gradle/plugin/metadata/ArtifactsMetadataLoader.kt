package com.releaseshub.gradle.plugin.metadata

import com.releaseshub.gradle.plugin.metadata.api.SheetService
import com.releaseshub.gradle.plugin.metadata.domain.ArtifactMetadata

class ArtifactsMetadataLoader {

    fun load(): Map<String, ArtifactMetadata> {
        val artifactsMap = mutableMapOf<String, ArtifactMetadata>()
        SheetService().getArtifactsMetadata().forEach {
            artifactsMap[it.getId()!!] = it
        }
        return artifactsMap
    }
}
