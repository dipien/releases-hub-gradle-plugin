package com.releaseshub.gradle.plugin.artifacts.fetch

import kotlin.math.max

class Version(version: String) : Comparable<Version> {

    private var version: String

    var baseVersion: String
    private set

    var versionClassifier: String? = null
    private set

    var releaseLifeCycle: ReleaseLifeCycle
    private set

    private val versionSplits: List<String>

    init {
        this.version = version.trim()
        // This hack is only used by google-api-services-androidpublisher
        if (this.version.startsWith("v")) {
            baseVersion = this.version
            releaseLifeCycle = ReleaseLifeCycle.STABLE
            val splits = mutableListOf<String>()
            baseVersion.split("-").forEach {
                splits.add(it.removePrefix("rev"))
            }
            versionSplits = splits
        } else {
            baseVersion = this.version.split(" ")[0]
            baseVersion = baseVersion.split("-")[0]
            if (this.version == baseVersion) {
                releaseLifeCycle = ReleaseLifeCycle.STABLE
            } else {
                versionClassifier = version.replaceFirst(baseVersion, "")
                if (versionClassifier!!.toLowerCase().contains("alpha")) {
                    releaseLifeCycle = ReleaseLifeCycle.ALPHA
                } else if (versionClassifier!!.toLowerCase().contains("beta")) {
                    releaseLifeCycle = ReleaseLifeCycle.BETA
                } else if (versionClassifier!!.toLowerCase().contains("rc")) {
                    releaseLifeCycle = ReleaseLifeCycle.RC
                } else {
                    releaseLifeCycle = ReleaseLifeCycle.UNKNOWN
                }
            }
            versionSplits = baseVersion.split(".")
        }
    }

    fun isStable(): Boolean {
        return releaseLifeCycle == ReleaseLifeCycle.STABLE
    }

    override fun compareTo(other: Version): Int {
        if (this.version == other.version) {
            return 0
        } else {
            val maxSize = max(versionSplits.size, other.versionSplits.size)
            for (i in 0 until maxSize) {
                val version1 = if (i < versionSplits.size) versionSplits[i] else "0"
                val version2 = if (i < other.versionSplits.size) other.versionSplits[i] else "0"
                if (version1 != version2) {
                    return VersionStringComparator.compare(version1, version2)
                }
            }
            return VersionStringComparator.compare(versionClassifier, other.versionClassifier)
        }
    }

    override fun toString(): String {
        return version
    }
}
