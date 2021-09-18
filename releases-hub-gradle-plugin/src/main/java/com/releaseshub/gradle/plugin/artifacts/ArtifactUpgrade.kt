package com.releaseshub.gradle.plugin.artifacts

class ArtifactUpgrade {

    companion object {
        const val GRADLE_ID = "gradle"
    }

    var name: String? = null
    var id: String? = null
    var groupId: String? = null
    var artifactId: String? = null
    var packaging: Packaging? = null
    var fromVersion: String? = null
    var toVersion: String? = null
    var fromSize: Long? = null
    var toSize: Long? = null
    var fromAndroidPermissions: List<String>? = null
    var toAndroidPermissions: List<String>? = null
    var toReleaseDate: Long? = null
    var sourceCodeUrl: String? = null
    var releaseNotesUrl: String? = null
    var documentationUrl: String? = null
    var issueTrackerUrl: String? = null
    var detailsUrl: String? = null
    var packages: List<String>? = null
    lateinit var artifactUpgradeStatus: ArtifactUpgradeStatus

    constructor(groupId: String, artifactId: String, fromVersion: String?) {
        this.id = groupId + "_" + artifactId
        this.groupId = groupId
        this.artifactId = artifactId
        this.fromVersion = fromVersion
    }

    constructor(id: String, fromVersion: String) {
        this.id = id
        this.fromVersion = fromVersion
    }

    fun match(includes: List<String>?, excludes: List<String>?): Boolean {
        val includeMatches = includes.isNullOrEmpty() || includes.find { match(it) } != null
        return if (includeMatches) {
            excludes.orEmpty().find { match(it) } == null
        } else {
            false
        }
    }

    private fun match(expression: String): Boolean {
        if (groupId != null) {
            val split = expression.split(":")
            val groupIdToMatch = split[0]
            val artifactIdToMatch = if (split.size > 1) split[1] else null
            return groupIdToMatch == groupId && (artifactIdToMatch == null || artifactIdToMatch == artifactId)
        } else {
            if (!expression.contains(":")) {
                return expression == id
            }
            return false
        }
    }

    fun isSnapshotVersion(): Boolean {
        return fromVersion!!.endsWith("-SNAPSHOT")
    }

    fun isDynamicVersion(): Boolean {
        return when {
            fromVersion!!.startsWith("[") -> true
            fromVersion!!.endsWith(")") -> true
            fromVersion!!.endsWith("+") -> true
            fromVersion!! == "+" -> true
            fromVersion!! == "latest.release" -> true
            else -> false
        }
    }

    override fun toString(): String {
        return if (groupId != null) "$groupId:$artifactId" else id!!
    }

    fun toFromVersionedString(): String {
        return if (groupId != null) "$groupId:$artifactId:$fromVersion" else "$id:$fromVersion"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArtifactUpgrade
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
