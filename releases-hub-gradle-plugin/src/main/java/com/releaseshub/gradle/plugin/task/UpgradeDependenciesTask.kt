package com.releaseshub.gradle.plugin.task

import com.jdroid.github.RepositoryId
import com.jdroid.github.client.GitHubClient
import com.jdroid.github.service.IssueService
import com.jdroid.github.service.PullRequestService
import com.releaseshub.gradle.plugin.artifacts.Artifact
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
        }

        val artifacts = mutableSetOf<Artifact>()
        val filesMap = mutableMapOf<String, List<String>>()

        dependenciesClassNames!!.forEach {
            val lines = project.rootProject.file(DEPENDENCIES_BASE_PATH + it).readLines()
            filesMap[DEPENDENCIES_BASE_PATH + it] = lines

            lines.forEach { line ->
                val artifact = DependenciesParser.extractArtifact(line)
                if (artifact != null && artifact.match(includes, excludes)) {
                    artifacts.add(artifact)
                }
            }
        }

        val artifactsToUpgrade = createArtifactsService().getArtifactsToUpgrade(artifacts.toList(), getRepositories())
        if (artifactsToUpgrade.isNotEmpty()) {

            if (pullRequestEnabled) {
                prepareGitBranch()
            }

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

            upgradeResults.forEach {
                log(" - ${it.artifact} ${it.artifact?.fromVersion} -> ${it.artifact?.toVersion}")
            }

            if (pullRequestEnabled) {
                createPullRequest(upgradeResults)
            }
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
                        val pullRequestBody = PullRequestGenerator.createBody(upgradeResults, pullRequest.body)
                        pullRequestService.editPullRequest(repositoryIdProvider, pullRequest.number, pullRequest.title, pullRequestBody, IssueService.STATE_OPEN, baseBranch)
                        log("The pull request #" + pullRequest.number + " already exists, editing it")
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
