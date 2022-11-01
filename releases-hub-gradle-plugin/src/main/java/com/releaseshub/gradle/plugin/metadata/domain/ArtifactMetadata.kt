package com.releaseshub.gradle.plugin.metadata.domain

import com.jdroid.java.domain.Entity

class ArtifactMetadata : Entity {

    var sourceCodeUrl: String? = null
    var releaseNotesUrl: String? = null
    var issueTrackerUrl: String? = null
    var documentationLink: String? = null
    constructor()
}
