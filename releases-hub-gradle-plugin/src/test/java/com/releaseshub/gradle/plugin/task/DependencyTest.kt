package com.releaseshub.gradle.plugin.task

import org.junit.Assert
import org.junit.Test

class DependencyTest {

	@Test
	fun noIncludesExcludesTest() {
		val dependency = Dependency("group1", "artifact1", "1.0.0")
		Assert.assertTrue(dependency.match(listOf(), listOf()))
	}

	@Test
	fun groupExcludeTest() {
		val dependency = Dependency("group1", "artifact1", "1.0.0")
		Assert.assertFalse(dependency.match(listOf(), listOf("group1")))
		Assert.assertTrue(dependency.match(listOf(), listOf("group2")))
	}

	@Test
	fun artifactExcludeTest() {
		val dependency = Dependency("group1", "artifact1", "1.0.0")
		Assert.assertFalse(dependency.match(listOf(), listOf("group1:artifact1")))
		Assert.assertTrue(dependency.match(listOf(), listOf("group1:artifact2")))
		Assert.assertTrue(dependency.match(listOf(), listOf("group2:artifact1")))
	}

	@Test
	fun groupIncludeTest() {
		val dependency = Dependency("group1", "artifact1", "1.0.0")
		Assert.assertTrue(dependency.match(listOf("group1"), listOf()))
		Assert.assertFalse(dependency.match(listOf("group2"), listOf()))
	}

	@Test
	fun artifactIncludeTest() {
		val dependency = Dependency("group1", "artifact1", "1.0.0")
		Assert.assertTrue(dependency.match(listOf("group1:artifact1"), listOf()))
		Assert.assertFalse(dependency.match(listOf("group1:artifact2"), listOf()))
		Assert.assertFalse(dependency.match(listOf("group2:artifact1"), listOf()))
	}

	@Test
	fun mixTest() {
		val dependency = Dependency("group1", "artifact1", "1.0.0")
		Assert.assertTrue(dependency.match(listOf("group1:artifact1"), listOf("group1:artifact2")))
		Assert.assertFalse(dependency.match(listOf("group1:artifact2"), listOf("group1:artifact1")))

		Assert.assertTrue(dependency.match(listOf("group1:artifact1"), listOf("group2")))
		Assert.assertFalse(dependency.match(listOf("group1:artifact2"), listOf("group2")))
		Assert.assertFalse(dependency.match(listOf("group3:artifact1"), listOf("group1")))

		Assert.assertTrue(dependency.match(listOf("group1"), listOf("group2")))
		Assert.assertFalse(dependency.match(listOf("group2"), listOf("group1")))
		Assert.assertFalse(dependency.match(listOf("group2"), listOf("group3")))

		Assert.assertTrue(dependency.match(listOf("group1"), listOf("group1:artifact2")))
		Assert.assertFalse(dependency.match(listOf("group1"), listOf("group1:artifact1")))
		Assert.assertFalse(dependency.match(listOf("group2"), listOf("group1:artifact1")))
		Assert.assertFalse(dependency.match(listOf("group2"), listOf("group1:artifact3")))
	}

}