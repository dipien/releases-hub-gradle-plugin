package com.releaseshub.gradle.plugin.artifacts.fetch

import java.util.Date

class Release {

    var version: String? = null
        set(value) {
            field = value
            lifeCycle = Version(version!!).releaseLifeCycle
        }
    var lifeCycle: ReleaseLifeCycle? = null
    var date: Date? = null

    fun isStable(): Boolean {
        return lifeCycle == ReleaseLifeCycle.STABLE
    }
}
