package com.releaseshub.gradle.plugin.task


import com.releaseshub.gradle.plugin.artifacts.Artifact
import com.releaseshub.gradle.plugin.artifacts.ArtifactsService
import com.releaseshub.gradle.plugin.common.AbstractTask


open class ListDependenciesToUpgradeTask : AbstractTask() {

	var dependenciesFilesPaths = mutableListOf<String>()
	var includes = mutableListOf<String>()
	var excludes = mutableListOf<String>()

	init {
		description = "List all dependencies to upgrade"
	}

	override fun onExecute() {
        val artifacts = mutableListOf<Artifact>()
		dependenciesFilesPaths.forEach {
			project.rootProject.file(it).forEachLine { line ->
				val artifact = DependenciesParser.extractArtifact(line)
				if (artifact != null && artifact.match(includes, excludes)) {
					artifacts.add(artifact)
				}
			}
		}

		ArtifactsService.getArtifactsToUpgrade(artifacts).forEach {
            println(" - $it -> ${it.toVersion}")
        }
	}

}
