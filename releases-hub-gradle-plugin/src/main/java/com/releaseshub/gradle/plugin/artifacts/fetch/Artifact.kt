package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.domain.Entity

class Artifact : Entity() {

    var groupId: String? = null
    var artifactId: String? = null
    var releases: MutableList<Release>? = null
    var repository: MavenArtifactRepository? = null

    fun getStableRelease(): Release? {
        return releases?.find { it.isStable() }
    }

    override fun toString(): String {
        return "$groupId:$artifactId"
    }
}
