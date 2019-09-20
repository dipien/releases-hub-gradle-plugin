package com.releaseshub.gradle.plugin.artifacts.fetch

class Release {

    var version: String? = null
        set(value) {
            field = value
            lifeCycle = Version(version!!).releaseLifeCycle
        }
    var lifeCycle: ReleaseLifeCycle? = null

    fun isStable(): Boolean {
        return lifeCycle == ReleaseLifeCycle.STABLE
    }

    fun getBaseVersion(): String {
        return Version(version!!).baseVersion
    }
}
