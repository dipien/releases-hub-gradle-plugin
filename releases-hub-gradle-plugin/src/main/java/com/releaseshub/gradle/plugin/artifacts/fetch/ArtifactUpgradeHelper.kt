package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.http.exception.ConnectionException
import com.jdroid.java.http.exception.HttpResponseException
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.LoggerHelper

object ArtifactUpgradeHelper {

    fun getArtifactsUpgrades(artifactsToCheck: List<ArtifactUpgrade>, repositories: List<MavenArtifactRepository>): List<ArtifactUpgrade> {
        val artifactsToUpgrade = mutableListOf<ArtifactUpgrade>()
        artifactsToCheck.forEach { artifactToCheck ->
            // TODO Add support for gradle artifacts on plugin side
            if (artifactToCheck.artifactId != null) {
                artifactsToUpgrade.add(getArtifactUpgrade(artifactToCheck, repositories))
            }
        }
        return artifactsToUpgrade.sortedBy { it.toReleaseDate }
    }

    private fun getArtifactUpgrade(artifactToCheck: ArtifactUpgrade, repositories: List<MavenArtifactRepository>): ArtifactUpgrade {
        val artifact = getArtifact(artifactToCheck, repositories)
        if (!artifact.releases.isNullOrEmpty()) {
            val release = getReleaseToUpgrade(artifactToCheck, artifact)
            return if (release != null) {
                artifactToCheck.toVersion = release.version!!
                artifactToCheck.toReleaseDate = release.date
                artifactToCheck.artifactUpgradeStatus = ArtifactUpgradeStatus.PENDING_UPGRADE
                artifactToCheck.repositoryUrl = artifact.repository?.url
                artifactToCheck
            } else {
                artifactToCheck.artifactUpgradeStatus = ArtifactUpgradeStatus.ALREADY_UPGRADED
                artifactToCheck
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

        val versions = mutableListOf<Version>()
        var lastUpdated: Long? = null

        var repositoriesToCheck = repositories
        if (artifactToCheck.isFromPluginDSL) {
            repositoriesToCheck = listOf(MavenArtifactRepository("Gradle Plugin Portal", "https://plugins.gradle.org/m2"))
        }
        repositoriesToCheck.forEach { repository ->
            try {
                val versioningMetadata = MavenArtifactRepositoryStrategy(repository).getVersioningMetadata(artifact)
                versions.addAll(versioningMetadata.versions.orEmpty())
                if (versioningMetadata.lastUpdated != null) {
                    if (lastUpdated == null || versioningMetadata.lastUpdated!!.time > lastUpdated!!) {
                        lastUpdated = versioningMetadata.lastUpdated!!.time
                        artifact.repository = repository
                    }
                }
            } catch (e: HttpResponseException) {
                LoggerHelper.logger.info("Error when fetching fetch for $artifact on repository ${repository.url}")
            } catch (e: ConnectionException) {
                LoggerHelper.logger.info("Timeout when fetching fetch for $artifact on repository ${repository.url}")
            }
        }

        if (versions.isNotEmpty()) {
            val release = Release()
            release.version = getLatestVersion(versions).toString()
            release.date = lastUpdated
            releases.add(release)
        }

        return artifact
    }

    fun getLatestVersion(versions: List<Version>): Version {
        var latestStableVersion: Version? = null
        var latestNotStableVersion: Version? = null
        for (version in versions) {
            if (version.isStable()) {
                if (latestStableVersion == null || version > latestStableVersion) {
                    latestStableVersion = version
                }
            } else {
                if (latestNotStableVersion == null || version > latestNotStableVersion) {
                    latestNotStableVersion = version
                }
            }
        }
        return latestStableVersion ?: latestNotStableVersion!!
    }

    private fun getReleaseToUpgrade(artifactToCheck: ArtifactUpgrade, artifact: Artifact): Release? {
        val stableRelease = artifact.getStableRelease()
        if (stableRelease != null) {
            val fromVersion = Version(artifactToCheck.fromVersion!!)
            val fromStableVersion = Version(fromVersion.baseVersion)
            if (Version(stableRelease.version!!) > fromStableVersion) {
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
