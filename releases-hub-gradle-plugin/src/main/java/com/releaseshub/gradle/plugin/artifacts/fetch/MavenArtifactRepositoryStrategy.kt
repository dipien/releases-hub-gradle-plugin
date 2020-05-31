package com.releaseshub.gradle.plugin.artifacts.fetch

class MavenArtifactRepositoryStrategy(private val mavenArtifactRepository: MavenArtifactRepository) : ArtifactRepositoryStrategy {

    override fun getVersioningMetadata(artifact: Artifact): VersioningMetadata {
        val service = MavenRepositoryService(mavenArtifactRepository.name, mavenArtifactRepository.url)
        return service.getVersioningMetadata(artifact)
    }
}
