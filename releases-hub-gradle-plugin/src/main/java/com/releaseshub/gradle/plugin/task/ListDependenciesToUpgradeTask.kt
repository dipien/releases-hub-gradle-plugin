package com.releaseshub.gradle.plugin.task


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
        val dependencies = mutableListOf<Dependency>()
		dependenciesFilesPaths.forEach {
			project.rootProject.file(it).forEachLine { line ->
				val dependency = DependencyExtractor.extractDependency(line)
				if (dependency != null && dependency.match(includes, excludes)) {
                    dependencies.add(dependency)
				}
			}
		}

        val artifacts = ArtifactsService.getArtifactsToUpdate(dependencies)
        artifacts.forEach {
            println(" - $it -> ${it.latestVersion}")
        }
	}

}
