package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.common.AbstractTask
import java.io.File
import org.gradle.api.Project

open class ValidateDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "validateDependencies"
    }

    lateinit var unusedExcludes: List<String>
    lateinit var unusedExtensionsToSearch: List<String>

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
                        log("- The dependency $artifact is a snapshot")
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
        project.rootProject.allprojects.forEach {
            sourcesDir.addAll(getSourceSets(it))
        }

        val excludes = unusedExcludes.plus("org.jetbrains.kotlin:kotlin-stdlib-jdk7").plus("com.pinterest:ktlint")
        val dependencyUsageSearcher = DependencyUsageSearcher(sourcesDir, unusedExtensionsToSearch)
        artifactsUpgrades.filter { it.match(listOf(), excludes) }.forEach { artifactUpgrade ->
            if (!dependencyUsageSearcher.isAnyPackageUsed(artifactUpgrade)) {
                log("- The dependency $artifactUpgrade seems to be unused on your project. See if you can safely remove it.")
                fail = true
            }
        }

        if (fail) {
            throw RuntimeException("Some errors were found on your dependencies")
        }
    }

    // TODO We should automatically search for projects source sets
    private fun getSourceSets(project: Project): List<File> {
        val paths = mutableListOf<String>()
        paths.add("src" + File.separator + "main" + File.separator + "java")
        paths.add("src" + File.separator + "main" + File.separator + "kotlin")
        paths.add("src" + File.separator + "main" + File.separator + "resources")
        paths.add("src" + File.separator + "release" + File.separator + "java")
        paths.add("src" + File.separator + "release" + File.separator + "kotlin")
        paths.add("src" + File.separator + "release" + File.separator + "resources")
        paths.add("src" + File.separator + "debug" + File.separator + "java")
        paths.add("src" + File.separator + "debug" + File.separator + "kotlin")
        paths.add("src" + File.separator + "debug" + File.separator + "resources")
        paths.add("src" + File.separator + "test" + File.separator + "java")
        paths.add("src" + File.separator + "test" + File.separator + "kotlin")
        paths.add("src" + File.separator + "test" + File.separator + "resources")
        paths.add("src" + File.separator + "androidTest" + File.separator + "java")
        paths.add("src" + File.separator + "androidTest" + File.separator + "kotlin")
        paths.add("src" + File.separator + "androidTest" + File.separator + "resources")

        val sourceSets = mutableListOf<File>()
        paths.forEach {
            val dir = File(project.projectDir, it)
            if (dir.exists()) {
                sourceSets.add(dir)
            }
        }
        return sourceSets.toList()
    }
}
