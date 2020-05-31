package com.releaseshub.gradle.plugin.artifacts.fetch

interface ArtifactRepositoryStrategy {

    fun getVersioningMetadata(artifact: Artifact): VersioningMetadata
}
