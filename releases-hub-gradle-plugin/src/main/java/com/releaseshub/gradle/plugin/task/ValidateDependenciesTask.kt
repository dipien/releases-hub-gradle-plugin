package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask
import java.io.File



open class ValidateDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "validateDependencies"
    }

    init {
        description = "Validate all dependencies"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
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

        val dependenciesParserResult = DependenciesParser.extractArtifacts(project, dependenciesBasePath!!, dependenciesClassNames!!, emptyList(), emptyList())
        val artifactsUpgrades = createAppService().getArtifactsToUpgrade(dependenciesParserResult.getAllArtifacts())

        val sourcesDir = mutableListOf<File>()
        // TODO We should automatically search for projects source sets
        project.rootProject.allprojects.forEach {
            var dir = File(it.projectDir, "src" + File.separator + "main" + File.separator + "java")
            if (dir.exists()) {
                sourcesDir.add(dir)
            }
            dir = File(it.projectDir, "src" + File.separator + "main" + File.separator + "kotlin")
            if (dir.exists()) {
                sourcesDir.add(dir)
            }
            dir = File(it.projectDir, "src" + File.separator + "main" + File.separator + "resources")
            if (dir.exists()) {
                sourcesDir.add(dir)
            }
            dir = File(it.projectDir, "src" + File.separator + "test" + File.separator + "java")
            if (dir.exists()) {
                sourcesDir.add(dir)
            }
            dir = File(it.projectDir, "src" + File.separator + "test" + File.separator + "kotlin")
            if (dir.exists()) {
                sourcesDir.add(dir)
            }
            dir = File(it.projectDir, "src" + File.separator + "test" + File.separator + "resources")
            if (dir.exists()) {
                sourcesDir.add(dir)
            }
        }

        val dependencyUsageSearcher = DependencyUsageSearcher(sourcesDir)
        artifactsUpgrades.forEach { artifactUpgrade ->
            if (!dependencyUsageSearcher.isDependencyDeclared(artifactUpgrade)) {
                log("- The dependency $artifactUpgrade seems to be not declared on your project. See if you can safely remove it.")
                fail = true
            }
            if (!dependencyUsageSearcher.isAnyPackageUsed(artifactUpgrade)) {
                log("- The dependency $artifactUpgrade seems to be unused on your project. See if you can safely remove it.")
                fail = true
            }
        }

        if (fail) {
            throw RuntimeException("Some errors were found on your dependencies")
        }
    }
}
