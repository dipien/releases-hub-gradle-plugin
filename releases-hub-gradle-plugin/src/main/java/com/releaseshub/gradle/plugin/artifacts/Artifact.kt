package com.releaseshub.gradle.plugin.artifacts

import com.jdroid.java.domain.Entity
import java.util.Date

class Artifact : Entity {

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

    constructor(id: String) : super(id) {}

    constructor() { }

    override fun toString(): String {
        return "$groupId:$artifactId:$previousVersion"
    }
}
