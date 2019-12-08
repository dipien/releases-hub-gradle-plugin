package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.http.exception.HttpResponseException
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.LoggerHelper

object ArtifactUpgradeHelper {

    fun getArtifactsUpgrades(artifactsToCheck: List<ArtifactUpgrade>, repositories: List<MavenArtifactRepository>): List<ArtifactUpgrade> {
        val artifactsToUpgrade = mutableListOf<ArtifactUpgrade>()
        artifactsToCheck.forEach { artifactToCheck ->
            artifactsToUpgrade.add(getArtifactUpgrade(artifactToCheck, repositories))
        }
        return artifactsToUpgrade
    }

    private fun getArtifactUpgrade(artifactToCheck: ArtifactUpgrade, repositories: List<MavenArtifactRepository>): ArtifactUpgrade {
        val artifact = getArtifact(artifactToCheck, repositories)
        if (!artifact.releases.isNullOrEmpty()) {
            val release = getReleaseToUpgrade(artifactToCheck, artifact)
            if (release != null) {
                artifactToCheck.toVersion = release.version!!
                artifactToCheck.artifactUpgradeStatus = ArtifactUpgradeStatus.PENDING_UPGRADE
                return artifactToCheck
            } else {
                artifactToCheck.artifactUpgradeStatus = ArtifactUpgradeStatus.ALREADY_UPGRADED
                return artifactToCheck
            }
        } else {
            artifactToCheck.artifactUpgradeStatus = ArtifactUpgradeStatus.NOT_FOUND
            return artifactToCheck
        }
    }

    private fun getArtifact(artifactToCheck: ArtifactUpgrade, repositories: List<MavenArtifactRepository>): Artifact {
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
            LoggerHelper.logger.info("Error when fetching fetch for $artifact on repository ${repository.name}")
        }
        return releases
    }

    private fun getReleaseToUpgrade(artifactToCheck: ArtifactUpgrade, artifact: Artifact): Release? {
        val stableRelease = artifact.getStableRelease()
        if (stableRelease != null) {
            val fromVersion = Version(artifactToCheck.fromVersion!!)
            val fromStableVersion = Version(fromVersion.baseVersion)
            if (stableRelease.version!! > fromStableVersion.toString()) {
                return stableRelease
            } else if (!fromVersion.isStable() && stableRelease.version!! == fromStableVersion.toString()) {
                return stableRelease
            }

            // TODO Add support for non stable versions upgrades
            // if (!fromVersion.isStable()) {
            //     val release = artifact.getReleaseWithSameBaseVersion(fromVersion)
            //     if (release != null && release.lifeCycle!! > fromVersion.releaseLifeCycle) {
            //         return release
            //     }
            // }
        }
        return null
    }
}
