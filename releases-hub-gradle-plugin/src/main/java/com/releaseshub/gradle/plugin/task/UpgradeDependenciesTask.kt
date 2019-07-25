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
				val artifact = DependenciesParser.extractArtifact(line)
				if (artifact != null && artifact.match(includes, excludes)) {
					artifacts.add(artifact)
				}
			}
		}

		val artifactsToUpgrade = ArtifactsService.getArtifactsToUpdate(artifacts.toList())

		val upgradeResults = mutableListOf<UpgradeResult>()
		filesMap.entries.forEach {
			File(it.key).bufferedWriter().use { out ->
				it.value.forEach { line ->
					val upgradeResult = DependenciesParser.upgradeDependency(line, artifactsToUpgrade)
					if (upgradeResult.upgraded) {
						upgradeResults.add(upgradeResult)
					}
					out.write(upgradeResult.line + "\n")
				}
			}
		}

		upgradeResults.forEach {
			println(it)
		}
	}

}
