package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade

class DependenciesParserResult {

    val artifacts = mutableSetOf<ArtifactUpgrade>()
    val dependenciesMap = mutableMapOf<String, List<String>>()

}