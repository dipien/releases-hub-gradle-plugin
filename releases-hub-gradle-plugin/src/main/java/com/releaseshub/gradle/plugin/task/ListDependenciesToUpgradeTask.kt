package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact
import com.releaseshub.gradle.plugin.common.AbstractTask

open class ListDependenciesToUpgradeTask : AbstractTask() {

    init {
        description = "List all dependencies to upgrade"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
        getExtension().validateDependenciesClassNames()

        val artifacts = mutableListOf<Artifact>()
        dependenciesClassNames!!.forEach {
            project.rootProject.file(DEPENDENCIES_BASE_PATH + it).forEachLine { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    artifacts.add(artifact)
                }
            }
        }

        createArtifactsService().getArtifactsToUpgrade(artifacts, getRepositories()).forEach {
            log(" - $it ${it.fromVersion} -> ${it.toVersion}")
        }
    }
}
