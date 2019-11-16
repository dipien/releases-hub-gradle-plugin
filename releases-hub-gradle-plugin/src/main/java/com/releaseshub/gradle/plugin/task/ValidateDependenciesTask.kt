package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask

open class ValidateDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "validateDependencies"
    }

    init {
        description = "Validate all dependencies"
    }

    override fun onExecute() {

        getExtension().validateDependenciesClassNames()

        var fail = false

        dependenciesClassNames!!.forEach {
            val dependencies = mutableListOf<String>()
            log(it)
            var failOnFile = false
            project.rootProject.file(dependenciesBasePath + it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null) {
                    if (dependencies.contains(artifact.toString())) {
                        fail = true
                        failOnFile = true
                        log("- The dependency $artifact is duplicated")
                    } else {
                        dependencies.add(artifact.toString())
                    }

                    if (artifact.isSnapshotVersion()) {
                        fail = true
                        failOnFile = true
                        log("- The dependency $artifact is an snapshot")
                    }

                    if (artifact.isDynamicVersion()) {
                        fail = true
                        failOnFile = true
                        log("- The dependency $artifact is using a dynamic version")
                    }
                }
            }

            if (dependencies.sortedWith(String.CASE_INSENSITIVE_ORDER) != dependencies) {
                fail = true
                failOnFile = true
                log("- The dependencies are not alphabetically sorted")
            }

            if (!failOnFile) {
                log("- No errors found")
            }
        }

        if (fail) {
            throw RuntimeException("Some errors were found on your dependencies")
        }
    }
}
