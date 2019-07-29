package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesTask : AbstractTask() {

    var dependenciesFilesPaths: List<String>? = null
    lateinit var includes: List<String>
    lateinit var excludes: List<String>

    init {
        description = "List all dependencies"
    }

    override fun onExecute() {

        getExtension().validateDependenciesFilesPaths()

        dependenciesFilesPaths!!.forEach {
            log(it)
            project.rootProject.file(it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    log(" - $artifact:${artifact.fromVersion}")
                }
            }
            log("")
        }
    }
}
