package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.dependencies.BuildSrcDependenciesExtractor

open class ListDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "listDependencies"
    }

    init {
        description = "List all dependencies"
    }

    override fun onExecute() {

        getExtension().validateDependenciesPaths()

        val extractor = BuildSrcDependenciesExtractor(dependenciesPaths!!)
        val dependenciesParserResult = extractor.extractArtifacts(project.rootProject.projectDir, includes, excludes)
        dependenciesParserResult.artifactsMap.forEach { (file, artifacts) ->
            if (artifacts.isNotEmpty()) {
                log(file)
                artifacts.forEach { artifact ->
                    log(" - $artifact:${artifact.fromVersion}")
                }
                log("")
            }
        }
    }
}
