package com.releaseshub.gradle.plugin.dependencies

import com.google.common.truth.Truth
import org.junit.Test

class DependenciesExtractorTest {

    @Test
    fun getDependencyMatchResultTest() {
        matchDependency(""""group:artifact:1.0.0"""")
        matchDependency(""" "group:artifact:1.0.0"""")
        matchDependency("""implementation("group:artifact:1.0.0")""")
        matchDependency("""implementation "group:artifact:1.0.0" """)
        matchDependency("""implementation 'group:artifact:1.0.0' """)
        matchDependency("""    const val OKHTTP = "group:artifact:1.0.0"""")
        notMatchDependency("""// "group:artifact:1.0.0"""")
        notMatchDependency("""//"group:artifact:1.0.0"""")
        notMatchDependency("""    @Deprecated("Use Firebase Installations: https://firebase.google.com/docs/projects/manage-installations#fid-iid")""")
    }

    private fun matchDependency(line: String) {
        val matchResult = DependenciesExtractor.getDependencyMatchResult(line)
        Truth.assertThat(matchResult).isNotNull()
        Truth.assertThat(matchResult!!.groupValues[1]).isEqualTo("group")
        Truth.assertThat(matchResult.groupValues[2]).isEqualTo("artifact")
        Truth.assertThat(matchResult.groupValues[3]).isEqualTo("1.0.0")
    }

    private fun notMatchDependency(line: String) {
        val matchResult = DependenciesExtractor.getDependencyMatchResult(line)
        Truth.assertThat(matchResult).isNull()
    }

    @Test
    fun getPluginsDSLMatchResultTest() {
        matchPlugin("""id("com.gradle.enterprise").version("3.7")""")
        matchPlugin(""" id("com.gradle.enterprise").version("3.7")""")
        matchPlugin("""id('com.gradle.enterprise').version('3.7')""")
        matchPlugin("""id "com.gradle.enterprise" version "3.7"""")
        matchPlugin("""id 'com.gradle.enterprise' version '3.7'""")
        notMatchPlugin("""// id("com.gradle.enterprise").version("3.7")""")
        notMatchPlugin("""//id("com.gradle.enterprise").version("3.7")""")
    }

    private fun matchPlugin(line: String) {
        val matchResult = DependenciesExtractor.getPluginsDSLMatchResult(line)
        Truth.assertThat(matchResult).isNotNull()
        Truth.assertThat(matchResult!!.groupValues[1]).isEqualTo("com.gradle.enterprise")
        Truth.assertThat(matchResult.groupValues[2]).isEqualTo("3.7")
    }

    private fun notMatchPlugin(line: String) {
        val matchResult = DependenciesExtractor.getPluginsDSLMatchResult(line)
        Truth.assertThat(matchResult).isNull()
    }
}
