package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact
import org.junit.Assert
import org.junit.Test

class DependenciesParserTest {

	@Test
	fun extractFromEmptyTest() {
		Assert.assertNull(DependenciesParser.extractArtifact(""))
		Assert.assertNull(DependenciesParser.extractArtifact(" "))
	}

	@Test
	fun extractFromCommentTest() {
		Assert.assertNull(DependenciesParser.extractArtifact("// this is a comment"))
		Assert.assertNull(DependenciesParser.extractArtifact("  // this is a comment"))
		Assert.assertNull(DependenciesParser.extractArtifact("""// this is a comment "com.jdroidtools:jdroid-java-core:2.0.0""""))
	}

	@Test
	fun extractWithoutVersionTest() {
		Assert.assertNull(DependenciesParser.extractArtifact("def libs = [:]"))
		Assert.assertNull(DependenciesParser.extractArtifact("  // rootProject.ext['libs'] = libs"))
	}

	@Test
	fun extractWithVersionTest() {
		val artifact = Artifact("com.jdroidtools", "jdroid-java-core", "2.0.0")
		Assert.assertEquals(artifact, DependenciesParser.extractArtifact("""libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""))
	}

	@Test
	fun upgradeCommentTest() {
		val artifactsToUpgrade = mutableListOf<Artifact>()
		val line = "// this is a comment"
		val upgradeResult = UpgradeResult(false, null, null, line)
		Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifactsToUpgrade))

		val artifact = Artifact()
		artifact.groupId = "a"
		artifact.artifactId = "b"
		artifact.latestVersion = "1.0.0"
		artifactsToUpgrade.add(artifact)
		Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifactsToUpgrade))
	}

	@Test
	fun notUpgradeTest() {
		val artifactsToUpgrade = mutableListOf<Artifact>()
		val line = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
		val upgradeResult = UpgradeResult(false, null, null, line)
		Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifactsToUpgrade))

		val artifact = Artifact()
		artifact.groupId = "a"
		artifact.artifactId = "b"
		artifact.latestVersion = "1.0.0"
		artifactsToUpgrade.add(artifact)
		Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifactsToUpgrade))
	}

	@Test
	fun upgradeTest() {
		val artifactsToUpgrade = mutableListOf<Artifact>()
		val artifact = Artifact()
		artifact.groupId = "com.jdroidtools"
		artifact.artifactId = "jdroid-java-core"
		artifact.latestVersion = "3.0.0"
		artifactsToUpgrade.add(artifact)
		val oldLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
		val newLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:3.0.0""""
		val upgradeResult = UpgradeResult(true, "2.0.0", artifact, newLine)
		Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(oldLine, artifactsToUpgrade))
	}
}