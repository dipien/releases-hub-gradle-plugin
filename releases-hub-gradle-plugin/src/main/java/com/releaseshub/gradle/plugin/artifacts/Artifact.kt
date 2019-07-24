package com.releaseshub.gradle.plugin.artifacts

import java.util.Date

class Artifact {

    var name: String? = null
    var owner: String? = null
    var groupId: String? = null
    var artifactId: String? = null
    var latestReleaseDate: Date? = null
    var previousVersion: String? = null
    var latestVersion: String? = null
    var sourceCodeUrl: String? = null
    var releaseNotesUrl: String? = null
    var documentationLinks: List<String>? = null

    constructor() { }

    constructor(groupId: String, artifactId: String, previousVersion: String) {
        this.groupId = groupId
        this.artifactId = artifactId
        this.previousVersion = previousVersion
    }

    fun match(includes : List<String>, excludes : List<String>) : Boolean {
        val includeMatches = includes.isEmpty() || includes.find { match(it) } != null
        return if (includeMatches) {
            excludes.find { match(it) } == null
        } else {
            false
        }
    }

    private fun match(expression: String) : Boolean {
        val split = expression.split(":")
        val groupIdToMatch = split[0]
        val artifactIdToMatch = if (split.size > 1) split[1] else null
        return groupIdToMatch == groupId && (artifactIdToMatch == null || artifactIdToMatch == artifactId)
    }

    override fun toString(): String {
        return "$groupId:$artifactId:$previousVersion"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Artifact

        if (groupId != other.groupId) return false
        if (artifactId != other.artifactId) return false
        if (previousVersion != other.previousVersion) return false
        if (latestVersion != other.latestVersion) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (groupId?.hashCode() ?: 0)
        result = 31 * result + (artifactId?.hashCode() ?: 0)
        result = 31 * result + (previousVersion?.hashCode() ?: 0)
        result = 31 * result + (latestVersion?.hashCode() ?: 0)
        return result
    }
}
