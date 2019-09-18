package com.releaseshub.gradle.plugin.artifacts

class ArtifactUpgradeBody {
    lateinit var artifactsToCheck: List<ArtifactUpgrade>
    lateinit var repositories: List<MavenArtifactRepository>
}