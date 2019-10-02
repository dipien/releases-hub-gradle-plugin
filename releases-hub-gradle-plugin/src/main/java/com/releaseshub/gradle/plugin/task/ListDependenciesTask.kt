package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesTask : AbstractTask() {

    init {
        description = "List all dependencies"
    }

    override fun onExecute() {

        getExtension().validateDependenciesClassNames()

        val dependenciesParserResult = DependenciesParser.extractArtifacts(project, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes)
        dependenciesParserResult.artifactsMap.forEach { (file, artifacts) ->
            log(file)
            artifacts.forEach { artifact ->
                log(" - $artifact:${artifact.fromVersion}")
            }
            log("")
        }
    }
}
