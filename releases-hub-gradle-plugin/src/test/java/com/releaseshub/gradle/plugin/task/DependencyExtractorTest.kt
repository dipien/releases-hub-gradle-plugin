package com.releaseshub.gradle.plugin.task

import org.junit.Assert
import org.junit.Test

class DependencyExtractorTest {

	@Test
	fun emptyTest() {
		Assert.assertNull(DependencyExtractor.extractDependency(""))
		Assert.assertNull(DependencyExtractor.extractDependency(" "))
	}

	@Test
	fun commentTest() {
		Assert.assertNull(DependencyExtractor.extractDependency("// this is a comment"))
		Assert.assertNull(DependencyExtractor.extractDependency("  // this is a comment"))
	}

	@Test
	fun withoutVersionTest() {
		Assert.assertNull(DependencyExtractor.extractDependency("def libs = [:]"))
		Assert.assertNull(DependencyExtractor.extractDependency("  // rootProject.ext['libs'] = libs"))
	}

	@Test
	fun withVersionTest() {
		val dependency = Dependency("com.jdroidtools", "jdroid-java-core", "2.0.0")
		Assert.assertEquals(dependency, DependencyExtractor.extractDependency("libs.jdroid_java_core = \"com.jdroidtools:jdroid-java-core:2.0.0\""))
	}
}