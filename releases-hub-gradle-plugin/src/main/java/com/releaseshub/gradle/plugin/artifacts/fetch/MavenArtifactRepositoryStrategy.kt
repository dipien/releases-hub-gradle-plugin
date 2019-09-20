package com.releaseshub.gradle.plugin.artifacts.fetch

class MavenArtifactRepositoryStrategy(private val mavenArtifactRepository: MavenArtifactRepository) : AbstractArtifactRepositoryStrategy() {

    override fun fetchVersions(artifact: Artifact): List<String> {
        val service = MavenRepositoryService(mavenArtifactRepository.name, mavenArtifactRepository.url)
        return service.getVersions(artifact)
    }
}
