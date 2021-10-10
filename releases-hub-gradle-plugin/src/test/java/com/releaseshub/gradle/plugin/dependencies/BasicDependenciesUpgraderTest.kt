package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.task.UpgradeResult
import org.junit.Assert
import org.junit.Test
import java.io.File

class BasicDependenciesUpgraderTest {

    @Test
    fun upgradeCommentTest() {
        val line = "// this is a comment"

        val artifact = ArtifactUpgrade("a", "b", "0.0.1")
        artifact.toVersion = "1.0.0"
        Assert.assertNull(upgradeDependenciesFile(line, artifact))
    }

    private fun upgradeDependenciesFile(line: String, artifactToUpgrade: ArtifactUpgrade): UpgradeResult? {
        val dependenciesFile = File.createTempFile("dependenciesFile", "tmp")
        dependenciesFile.writeText(line)
        return BasicDependenciesUpgrader().upgradeDependenciesFile(dependenciesFile, artifactToUpgrade)
    }

    @Test
    fun notUpgradeTest() {
        val line = """libs.sample = "com.dipien:sample:2.0.0""""

        val artifact = ArtifactUpgrade("a", "b", "0.0.1")
        artifact.toVersion = "1.0.0"
        Assert.assertNull(upgradeDependenciesFile(line, artifact))
    }

    @Test
    fun notUpgradeTest2() {
        val line = """libs.sample = "com.dipien:sample:2.0.0""""

        val artifact = ArtifactUpgrade("com.dipien", "sample")
        artifact.toVersion = "2.0.0"
        Assert.assertNull(upgradeDependenciesFile(line, artifact))
    }

    @Test
    fun upgradeTest() {
        val artifact = ArtifactUpgrade("com.dipien", "sample", "2.0.0")
        artifact.toVersion = "3.0.0"
        val oldLine = """libs.sample = "com.dipien:sample:2.0.0""""
        val newLine = """libs.sample = "com.dipien:sample:3.0.0""""
        val upgradeResult = UpgradeResult(true, artifact, newLine)
        Assert.assertEquals(upgradeResult, upgradeDependenciesFile(oldLine, artifact))
    }

    @Test
    fun upgradeTest2() {
        val artifact = ArtifactUpgrade("com.dipien", "sample", "1.0.0")
        artifact.toVersion = "3.0.0"

        val artifactResult = ArtifactUpgrade("com.dipien", "sample", "2.0.0")
        artifactResult.toVersion = "3.0.0"

        val oldLine = """libs.sample = "com.dipien:sample:2.0.0""""
        val newLine = """libs.sample = "com.dipien:sample:3.0.0""""
        val upgradeResult = UpgradeResult(true, artifactResult, newLine)
        Assert.assertEquals(upgradeResult, upgradeDependenciesFile(oldLine, artifact))
    }
}
