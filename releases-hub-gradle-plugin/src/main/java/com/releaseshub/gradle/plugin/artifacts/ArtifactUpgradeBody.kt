package com.releaseshub.gradle.plugin.artifacts

class ArtifactUpgradeBody {
    lateinit var artifactsToCheck: List<Artifact>
    lateinit var repositories: List<MavenArtifactRepository>
}