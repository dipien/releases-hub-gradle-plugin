package com.releaseshub.gradle.plugin.task


import com.releaseshub.gradle.plugin.artifacts.Artifact
import com.releaseshub.gradle.plugin.artifacts.ArtifactsService
import com.releaseshub.gradle.plugin.common.AbstractTask
import java.io.File

open class UpgradeDependenciesTask : AbstractTask() {

	var dependenciesFilesPaths = mutableListOf<String>()
	var includes = mutableListOf<String>()
	var excludes = mutableListOf<String>()

	init {
		description = "Upgrade dependencies"
	}

	override fun onExecute() {

		val artifacts = mutableSetOf<Artifact>()
		val filesMap = mutableMapOf<String, List<String>>()

		dependenciesFilesPaths.forEach {
			val lines = project.rootProject.file(it).readLines()
			filesMap[it] = lines

			lines.forEach { line ->
				val artifact = ArtifactExtractor.extractArtifact(line)
				if (artifact != null && artifact.match(includes, excludes)) {
					artifacts.add(artifact)
				}
			}
		}

		val artifactsToUpgrade = ArtifactsService.getArtifactsToUpdate(artifacts.toList())

		filesMap.entries.forEach {
			File(it.key).bufferedWriter().use { out ->
				it.value.forEach { line ->
					out.write(line + "\n")
				}
			}
		}
	}

}
