package com.releaseshub.gradle.plugin.task

import com.jdroid.github.RepositoryId
import com.jdroid.github.client.GitHubClient
import com.jdroid.github.service.IssueService
import com.jdroid.github.service.PullRequestService
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask
import java.io.File
import java.io.IOException

open class UpgradeDependenciesTask : AbstractTask() {

    var baseBranch: String? = null
    var headBranch: String? = null
    var commitMessage: String? = null
    var pullRequestTitle: String? = null
    var pullRequestEnabled: Boolean = false

    var gitHubUserName: String? = null
    var gitHubUserEmail: String? = null
    var gitHubRepositoryOwner: String? = null
    var gitHubRepositoryName: String? = null
    var gitHubWriteToken: String? = null

    init {
        description = "Upgrade dependencies"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
        getExtension().validateDependenciesClassNames()

        if (pullRequestEnabled) {
            getExtension().validateBaseBranch()
            getExtension().validateHeadBranch()
            getExtension().validateCommitMessage()
            getExtension().validatePullRequestTitle()
            getExtension().validateGitHubRepositoryOwner()
            getExtension().validateGitHubRepositoryName()
            getExtension().validateGitHubWriteToken()

            prepareGitBranch()
        }

        val artifacts = mutableSetOf<ArtifactUpgrade>()
        val filesMap = mutableMapOf<String, List<String>>()

        dependenciesClassNames!!.forEach {
            val lines = project.rootProject.file(dependenciesBasePath + it).readLines()
            filesMap[dependenciesBasePath + it] = lines

            lines.forEach { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    artifacts.add(artifact)
                }
            }
        }

        val artifactsToUpgrade = createArtifactsService().getArtifactsUpgrades(artifacts.toList(), getRepositories()).filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.PENDING_UPGRADE }
        if (artifactsToUpgrade.isNotEmpty()) {
            val upgradeResults = mutableListOf<UpgradeResult>()
            filesMap.entries.forEach {
                File(it.key).bufferedWriter().use { out ->
                    it.value.forEach { line ->
                        val upgradeResult = DependenciesParser.upgradeDependency(line, artifactsToUpgrade)
                        if (upgradeResult.upgraded) {
                            upgradeResults.add(upgradeResult)
                        }
                        out.write(upgradeResult.line + "\n")
                    }
                }
            }

            if (upgradeResults.isNullOrEmpty()) {
                log("No dependencies upgraded")
            } else {
                log("Dependencies upgraded:")
                upgradeResults.forEach {
                    log(" - ${it.artifactUpgrade} ${it.artifactUpgrade?.fromVersion} -> ${it.artifactUpgrade?.toVersion}")
                }
            }

            if (pullRequestEnabled) {
                createPullRequest(upgradeResults)
            }
        } else {
            log("No dependencies upgraded")
        }
    }

    private fun prepareGitBranch() {
        gitHubUserName?.let {
            gitHelper.configUserName(it)
        }
        gitHubUserEmail?.let {
            gitHelper.configUserEmail(it)
        }

        gitHelper.checkout(baseBranch!!)
        gitHelper.pull()

        // Local headBranch cleanup
        commandExecutor.execute("git branch -D $headBranch", project.rootProject.projectDir, true, true)
        gitHelper.prune()
        val execResult = commandExecutor.execute("git checkout $headBranch", project.rootProject.projectDir, true, true)
        if (!execResult.isSuccessful()) {
            gitHelper.createBranch(headBranch!!)
        }

        // Try to merge from baseBranch to headBranch
        // TODO If there is a conflict, it will fail. Add an error message here telling that the dev need to merge and resolve the conflicts
        gitHelper.merge(baseBranch!!)
    }

    private fun createPullRequest(upgradeResults: List<UpgradeResult>) {
        gitHelper.addAll()

        val execResult = commandExecutor.execute("git commit -m \"$commitMessage\"", project.rootProject.projectDir, true, true)
        when {
            execResult.isSuccessful() -> {
                commandExecutor.execute("git push origin HEAD:$headBranch")
                log("The changes were pushed to $headBranch branch.")

                val client = GitHubClient()
                client.setSerializeNulls(false)
                client.setOAuth2Token(gitHubWriteToken)

                val repositoryIdProvider = RepositoryId.create(gitHubRepositoryOwner, gitHubRepositoryName)
                val pullRequestService = PullRequestService(client)

                try {
                    var pullRequest = pullRequestService.getPullRequest(repositoryIdProvider, IssueService.STATE_OPEN, "$gitHubRepositoryOwner:$headBranch", baseBranch)
                    if (pullRequest == null) {
                        val pullRequestBody = PullRequestGenerator.createBody(upgradeResults)
                        pullRequest = pullRequestService.createPullRequest(repositoryIdProvider, pullRequestTitle, pullRequestBody, headBranch, baseBranch)
                        log("The pull request #" + pullRequest!!.number + " was successfully created.")
                    } else {
                        val pullRequestComment = PullRequestGenerator.createComment(upgradeResults)
                        val issueService = IssueService(client)
                        issueService.createComment(repositoryIdProvider, pullRequest.number, pullRequestComment)
                        log("The pull request #" + pullRequest.number + " already exists, adding a comment")
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
            execResult.getStandardOutput().contains("nothing to commit") -> log("Nothing new to commit.")
            else -> throw RuntimeException("Git commit command failed with exit value: " + execResult.exitValue)
        }
    }
}
