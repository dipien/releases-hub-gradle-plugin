package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.junit.Assert
import org.junit.Test

class DependenciesUpgraderTest {

    @Test
    fun upgradeCommentTest() {
        val line = "// this is a comment"
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade("a", "b", "0.0.1")
        artifact.toVersion = "1.0.0"
        Assert.assertEquals(upgradeResult, DependenciesUpgrader.upgradeDependency(line, artifact))
    }

    @Test
    fun notUpgradeTest() {
        val line = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade("a", "b", "0.0.1")
        artifact.toVersion = "1.0.0"
        Assert.assertEquals(upgradeResult, DependenciesUpgrader.upgradeDependency(line, artifact))
    }

    @Test
    fun notUpgradeTest2() {
        val line = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade("com.jdroidtools", "jdroid-java-core")
        artifact.toVersion = "2.0.0"
        Assert.assertEquals(upgradeResult, DependenciesUpgrader.upgradeDependency(line, artifact))
    }

    @Test
    fun upgradeTest() {
        val artifact = ArtifactUpgrade("com.jdroidtools", "jdroid-java-core", "2.0.0")
        artifact.toVersion = "3.0.0"
        val oldLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val newLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:3.0.0""""
        val upgradeResult = UpgradeResult(true, artifact, newLine)
        Assert.assertEquals(upgradeResult, DependenciesUpgrader.upgradeDependency(oldLine, artifact))
    }

    @Test
    fun upgradeTest2() {
        val artifact = ArtifactUpgrade("com.jdroidtools", "jdroid-java-core", "1.0.0")
        artifact.toVersion = "3.0.0"

        val artifactResult = ArtifactUpgrade("com.jdroidtools", "jdroid-java-core", "2.0.0")
        artifactResult.toVersion = "3.0.0"

        val oldLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val newLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:3.0.0""""
        val upgradeResult = UpgradeResult(true, artifactResult, newLine)
        Assert.assertEquals(upgradeResult, DependenciesUpgrader.upgradeDependency(oldLine, artifact))
    }
}
