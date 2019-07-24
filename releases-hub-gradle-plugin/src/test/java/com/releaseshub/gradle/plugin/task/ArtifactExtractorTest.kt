package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.Artifact
import org.junit.Assert
import org.junit.Test

class ArtifactExtractorTest {

	@Test
	fun emptyTest() {
		Assert.assertNull(ArtifactExtractor.extractArtifact(""))
		Assert.assertNull(ArtifactExtractor.extractArtifact(" "))
	}

	@Test
	fun commentTest() {
		Assert.assertNull(ArtifactExtractor.extractArtifact("// this is a comment"))
		Assert.assertNull(ArtifactExtractor.extractArtifact("  // this is a comment"))
	}

	@Test
	fun withoutVersionTest() {
		Assert.assertNull(ArtifactExtractor.extractArtifact("def libs = [:]"))
		Assert.assertNull(ArtifactExtractor.extractArtifact("  // rootProject.ext['libs'] = libs"))
	}

	@Test
	fun withVersionTest() {
		val artifact = Artifact("com.jdroidtools", "jdroid-java-core", "2.0.0")
		Assert.assertEquals(artifact, ArtifactExtractor.extractArtifact("libs.jdroid_java_core = \"com.jdroidtools:jdroid-java-core:2.0.0\""))
	}
}