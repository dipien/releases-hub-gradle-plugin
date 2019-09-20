package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.collections.Maps

abstract class AbstractArtifactRepositoryStrategy {

    fun getLatestVersions(artifact: Artifact): List<Version> {
        var latestStableVersion: Version? = null
        val versionsMap = Maps.newLinkedHashMap<String, Version>()
        for (stringVersion in fetchVersions(artifact)) {
            val version = Version(stringVersion)
            if (version.isStable()) {
                if (latestStableVersion == null || version > latestStableVersion) {
                    latestStableVersion = version
                }
            } else {
                val versionFromMap = versionsMap[version.baseVersion]
                if (versionFromMap == null || version > versionFromMap) {
                    versionsMap[version.baseVersion] = version
                }
            }
        }
        val latestVersions = if (latestStableVersion != null) mutableListOf(latestStableVersion) else mutableListOf()
        latestVersions.addAll(versionsMap.values.filter { latestStableVersion == null || it > latestStableVersion })
        return latestVersions.toList().sorted()
    }

    protected abstract fun fetchVersions(artifact: Artifact): List<String>
}
