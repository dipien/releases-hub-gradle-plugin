package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.domain.Entity

class Artifact : Entity {

    var groupId: String? = null
    var artifactId: String? = null
    var releases: MutableList<Release>? = null

    constructor(id: String) : super(id)

    constructor()

    fun getStableRelease(): Release? {
        return releases?.find { it.isStable() }
    }

    fun getReleaseWithSameBaseVersion(version: Version): Release? {
        return releases?.find { it.getBaseVersion() == version.baseVersion }
    }

    fun getRelease(latestVersion: Version): Release? {
        return if (latestVersion.isStable()) { getStableRelease() } else { getReleaseWithSameBaseVersion(latestVersion) }
    }

    fun getOlderReleasesWithSameBaseVersion(release: Release): Collection<Release> {
        val version = Version(release.version!!)
        return releases?.filter { it.version != version.toString() && it.getBaseVersion() == version.baseVersion && it.lifeCycle!!.ordinal < release.lifeCycle!!.ordinal } ?: listOf()
    }

    fun containsVersion(version: Version): Boolean {
        return releases?.find { it.version == version.toString() } != null
    }

    override fun toString(): String {
        return "$groupId:$artifactId"
    }
}
