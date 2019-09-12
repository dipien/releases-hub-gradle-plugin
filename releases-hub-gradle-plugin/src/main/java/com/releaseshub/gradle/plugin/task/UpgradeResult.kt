package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact

data class UpgradeResult(val upgraded: Boolean, val artifact: Artifact?, val line: String)