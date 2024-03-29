package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.junit.Assert
import org.junit.Test

class PullRequestGeneratorTest {

    @Test
    fun singleArtifactTest() {
        val upgradeResults = mutableListOf<UpgradeResult>()
        val artifact = ArtifactUpgrade("a", "b", "1.4.0")
        artifact.toVersion = "2.0.1"
        upgradeResults.add(UpgradeResult(true, artifact, ""))

        val body = PullRequestGenerator.createBody(upgradeResults, "1.2.3")
        Assert.assertEquals("""## Dependencies upgrades
### a:b
* **Version:** `1.4.0` -> `2.0.1`

---
This pull request was automatically generated by **[Releases Hub Gradle Plugin v1.2.3](https://github.com/dipien/releases-hub-gradle-plugin)**""".trimMargin(), body)
        val comment = PullRequestGenerator.createComment(upgradeResults)
        Assert.assertEquals("""### a:b
* **Version:** `1.4.0` -> `2.0.1`

""".trimMargin(), comment)
    }

    @Test
    fun twoArtifactsTest() {
        val upgradeResults = mutableListOf<UpgradeResult>()
        val artifact = ArtifactUpgrade("a", "b", "1.4.0")
        artifact.toVersion = "2.0.1"
        upgradeResults.add(UpgradeResult(true, artifact, ""))

        val artifact2 = ArtifactUpgrade("c", "d", "1.0.0")
        artifact2.toVersion = "1.0.1"
        upgradeResults.add(UpgradeResult(true, artifact2, ""))

        val body = PullRequestGenerator.createBody(upgradeResults, "1.2.3")
        Assert.assertEquals("""## Dependencies upgrades
### a:b
* **Version:** `1.4.0` -> `2.0.1`
### c:d
* **Version:** `1.0.0` -> `1.0.1`

---
This pull request was automatically generated by **[Releases Hub Gradle Plugin v1.2.3](https://github.com/dipien/releases-hub-gradle-plugin)**""".trimMargin(), body)
        val comment = PullRequestGenerator.createComment(upgradeResults)
        Assert.assertEquals("""### a:b
* **Version:** `1.4.0` -> `2.0.1`
### c:d
* **Version:** `1.0.0` -> `1.0.1`

""".trimMargin(), comment)
    }
}
