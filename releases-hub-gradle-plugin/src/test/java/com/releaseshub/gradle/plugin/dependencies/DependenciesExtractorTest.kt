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
}
