package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesTask : AbstractTask() {

    init {
        description = "List all dependencies"
    }

    override fun onExecute() {

        getExtension().validateDependenciesClassNames()

        dependenciesClassNames!!.forEach {
            log(it)
            project.rootProject.file(DEPENDENCIES_BASE_PATH + it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    log(" - $artifact:${artifact.fromVersion}")
                }
            }
            log("")
        }
    }
}
