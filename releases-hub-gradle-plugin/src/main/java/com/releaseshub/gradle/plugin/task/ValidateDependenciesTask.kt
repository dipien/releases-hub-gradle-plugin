package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask

open class ValidateDependenciesTask : AbstractTask() {

    init {
        description = "Validate all dependencies"
    }

    override fun onExecute() {

        getExtension().validateDependenciesClassNames()

        var fail = false
        val duplicatedDependenciesMap = hashMapOf<String, MutableList<String>>()

        dependenciesClassNames!!.forEach {
            val dependencies = mutableListOf<String>()
            project.rootProject.file(dependenciesBasePath + it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null) {
                    if (dependencies.contains(artifact.toString())) {
                        val list = duplicatedDependenciesMap.getOrDefault(dependenciesBasePath + it, mutableListOf())
                        list.add(artifact.toString())
                        duplicatedDependenciesMap[dependenciesBasePath + it] = list
                    } else {
                        dependencies.add(artifact.toString())
                    }
                }
            }

            if (dependencies.sortedWith(String.CASE_INSENSITIVE_ORDER) != dependencies) {
                fail = true
                log("The dependencies on $dependenciesBasePath$it are not alphabetically sorted")
            }
        }

        if (duplicatedDependenciesMap.isNotEmpty()) {
            fail = true
            duplicatedDependenciesMap.entries.forEach { (dependenciesPath, dependencies) ->
                log("The following dependencies are duplicated on $dependenciesPath")
                dependencies.forEach {
                    log("- $it")
                }
            }
        }

        if (fail) {
            throw RuntimeException("Some errors were found on your dependencies")
        }
    }
}
