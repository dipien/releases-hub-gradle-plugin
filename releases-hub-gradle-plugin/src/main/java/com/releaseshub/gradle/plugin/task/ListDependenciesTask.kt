package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.dependencies.BasicDependenciesExtractor

open class ListDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "listDependencies"
    }

    init {
        description = "List all dependencies"
    }

    override fun onExecute() {

        val extractor = BasicDependenciesExtractor(getAllDependenciesPaths())
        val dependenciesParserResult = extractor.extractArtifacts(project.rootProject.projectDir, includes, excludes)

        getAllDependenciesPaths().forEach {
            val artifacts = dependenciesParserResult.artifactsMap[it]
            log(it)
            if (artifacts.isNullOrEmpty()) {
                log(" - No dependencies found here")
            } else {
                artifacts.forEach { artifact ->
                    log(" - $artifact:${artifact.fromVersion}")
                }
            }
            log("")
        }
    }
}
