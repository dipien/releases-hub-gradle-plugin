package com.releaseshub.gradle.plugin.task


import com.releaseshub.gradle.plugin.common.AbstractTask
import org.gradle.api.logging.LogLevel


open class ListDependenciesTask : AbstractTask() {

	var dependenciesFilesPaths = mutableListOf<String>()
	var includes = mutableListOf<String>()
	var excludes = mutableListOf<String>()

	init {
		description = "List all dependencies"
	}

	override fun onExecute() {
		dependenciesFilesPaths.forEach {
			logger.log(LogLevel.LIFECYCLE, "$it dependencies")
			project.rootProject.file(it).forEachLine { line ->
				val dependency = DependencyExtractor.extractDependency(line)
				if (dependency != null && dependency.match(includes, excludes)) {
					println(dependency)
				}
			}
		}
	}

}
