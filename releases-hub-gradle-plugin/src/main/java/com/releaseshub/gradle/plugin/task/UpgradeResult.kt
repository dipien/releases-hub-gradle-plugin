package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade

data class UpgradeResult(val upgraded: Boolean, val artifactUpgrade: ArtifactUpgrade?, val line: String)
