package com.releaseshub.gradle.plugin.artifacts.fetch

abstract class AbstractArtifactRepositoryStrategy {

    fun getAllVersions(artifact: Artifact): List<Version> {
        return fetchVersions(artifact).map { Version(it) }
    }

    protected abstract fun fetchVersions(artifact: Artifact): List<String>
}
