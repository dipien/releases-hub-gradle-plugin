package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.http.exception.HttpResponseException
import com.jdroid.java.utils.LoggerUtils
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade

object ArtifactUpgradeHelper {

    private val LOGGER = LoggerUtils.getLogger(ArtifactUpgradeHelper::class.java)

    fun getArtifactsToUpgrade(artifactsToCheck: List<ArtifactUpgrade>, repositories: List<MavenArtifactRepository>): List<ArtifactUpgrade> {
        val artifactsToUpgrade = mutableListOf<ArtifactUpgrade>()
        artifactsToCheck.forEach { artifactToCheck ->
            val artifactUpgrade = getArtifactUpgradeOrNull(artifactToCheck, repositories)
            if (artifactUpgrade != null) {
                artifactsToUpgrade.add(artifactUpgrade)
            }
        }
        return artifactsToUpgrade
    }

    private fun getArtifactUpgradeOrNull(artifactToCheck: ArtifactUpgrade, repositories: List<MavenArtifactRepository>): ArtifactUpgrade? {
        val artifact = getArtifact(artifactToCheck, repositories)
        if (artifact != null) {
            val release = getReleaseToUpgrade(artifactToCheck, artifact)
            if (release != null) {
                artifactToCheck.toVersion = release.version!!
                return artifactToCheck
            } else {
                return null
            }
        } else {
            return null
        }
    }

    private fun getArtifact(artifactToCheck: ArtifactUpgrade, repositories: List<MavenArtifactRepository>): Artifact? {
        val artifact = Artifact()
        artifact.groupId = artifactToCheck.groupId
        artifact.artifactId = artifactToCheck.artifactId
        val releases = mutableListOf<Release>()
        artifact.releases = releases

        run loop@{
            repositories.forEach {
                releases.addAll(getArtifactReleases(artifact, it))
                // TODO We should search on all the repositories for the latest release
                if (artifact.getStableRelease() != null) {
                    return@loop
                }
            }
        }

        if (releases.isEmpty()) {
            LOGGER.warn("Artifact $artifact not found on any repository")
            return null
        }
        return artifact
    }

    private fun getArtifactReleases(artifact: Artifact, repository: MavenArtifactRepository): List<Release> {
        val releases = mutableListOf<Release>()
        try {
            for (latestVersion in MavenArtifactRepositoryStrategy(repository).getLatestVersions(artifact)) {
                val release = Release()
                release.version = latestVersion.toString()
                releases.add(release)
            }
        } catch (e: HttpResponseException) {
            LOGGER.info("Error when fetching fetch for $artifact on repository ${repository.name}")
        }
        return releases
    }

    private fun getReleaseToUpgrade(artifactToCheck: ArtifactUpgrade, artifact: Artifact): Release? {
        val fromVersion = Version(artifactToCheck.fromVersion!!)
        val fromStableVersion = Version(fromVersion.baseVersion)
        if (artifact.getStableRelease()?.version!! > fromStableVersion.toString()) {
            return artifact.getStableRelease()
        } else if (!fromVersion.isStable() && artifact.getStableRelease()?.version!! == fromStableVersion.toString()) {
            return artifact.getStableRelease()
        }

        // TODO Add support for non stable versions upgrades
        // if (!fromVersion.isStable()) {
        //     val release = artifact.getReleaseWithSameBaseVersion(fromVersion)
        //     if (release != null && release.lifeCycle!! > fromVersion.releaseLifeCycle) {
        //         return release
        //     }
        // }

        return null
    }
}