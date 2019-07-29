package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact
import com.releaseshub.gradle.plugin.artifacts.ArtifactsService
import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesToUpgradeTask : AbstractTask() {

    var dependenciesFilesPaths: List<String>? = null
    lateinit var includes: List<String>
    lateinit var excludes: List<String>

    init {
        description = "List all dependencies to upgrade"
    }

    override fun onExecute() {

        getExtension().validateDependenciesFilesPaths()

        val artifacts = mutableListOf<Artifact>()
        dependenciesFilesPaths!!.forEach {
            project.rootProject.file(it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    artifacts.add(artifact)
                }
            }
        }

        ArtifactsService.getArtifactsToUpgrade(artifacts).forEach {
            log(" - $it ${it.fromVersion} -> ${it.toVersion}")
        }
    }
}