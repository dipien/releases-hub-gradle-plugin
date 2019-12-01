package com.releaseshub.gradle.plugin.task

import com.jdroid.github.RepositoryId
import com.jdroid.github.client.GitHubClient
import com.jdroid.github.service.IssueService
import com.jdroid.github.service.LabelsService
import com.jdroid.github.service.PullRequestService
import com.jdroid.github.service.ReviewRequestsService
import com.jdroid.java.concurrent.ExecutorUtils
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

open class UpgradeDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "upgradeDependencies"
    }

    var baseBranch: String? = null
    var headBranchPrefix: String? = null
    var pullRequestEnabled: Boolean = false
    var pullRequestsMax: Int? = null
    var pullRequestLabels: List<String>? = null
    var pullRequestReviewers: List<String>? = null
    var pullRequestTeamReviewers: List<String>? = null
    var gitHubUserName: String? = null
    var gitHubUserEmail: String? = null
    var gitHubRepositoryOwner: String? = null
    var gitHubRepositoryName: String? = null
    var gitHubWriteToken: String? = null
    var gitHubApiHostName: String? = null

    init {
        description = "Upgrade dependencies"
    }

    override fun onExecute() {

        getExtension().validateServerName()
        getExtension().validateUserToken()
        getExtension().validateDependenciesClassNames()

        if (pullRequestEnabled) {
            getExtension().validateBaseBranch()
            getExtension().validateHeadBranchPrefix()
            getExtension().validateGitHubRepositoryOwner()
            getExtension().validateGitHubRepositoryName()
            getExtension().validateGitHubWriteToken()
        }

        val dependenciesParserResult = DependenciesParser.extractArtifacts(project, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes)

        val artifactsToUpgrade = createArtifactsService().getArtifactsUpgrades(dependenciesParserResult.getAllArtifacts(), getRepositories()).filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.PENDING_UPGRADE }

        if (artifactsToUpgrade.isNotEmpty()) {

            var groupsToUpgrade = artifactsToUpgrade.groupBy { if (pullRequestEnabled) it.groupId else null }.entries.toList()

            if (pullRequestEnabled) {
                configureGit()
                val totalSize = groupsToUpgrade.size
                groupsToUpgrade = groupsToUpgrade.take(pullRequestsMax!!)
                log("Creating ${groupsToUpgrade.size} of $totalSize possible pull requests. Increment the \"pullRequestsMax\" property if you want more pull requests created by task execution.")
                log("")
            }

            log("Dependencies upgraded:")

            groupsToUpgrade.forEach { (groupId, artifactsToUpgradeByGroup) ->

                val group: String = groupId ?: artifactsToUpgradeByGroup.first().toString()

                val headBranch = headBranchPrefix + group.replace(".", "_", true)

                var branchCreated = true
                if (pullRequestEnabled) {
                    branchCreated = prepareGitBranch(headBranch)
                }

                var dependenciesLinesMap = dependenciesParserResult.dependenciesLinesMap
                if (!branchCreated) {
                    dependenciesLinesMap = DependenciesParser.extractArtifacts(project, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes).dependenciesLinesMap
                }
                val upgradeResults = upgradeDependencies(dependenciesLinesMap, artifactsToUpgradeByGroup)
                if (pullRequestEnabled) {
                    if (upgradeResults.isNotEmpty()) {
                        createPullRequest(upgradeResults, headBranch, groupId, group)
                    } else {
                        if (!branchCreated) {
                            val execResult = commandExecutor.execute("git push origin HEAD:$headBranch", project.rootProject.projectDir, logStandardOutput = true, ignoreExitValue = true)
                            if (execResult.isSuccessful()) {
                                log("Merge pushed to $headBranch branch.")
                            }
                        }
                    }
                }
            }
        } else {
            log("No dependencies upgraded")
        }
    }

    private fun configureGit() {
        gitHubUserName?.let {
            gitHelper.configUserName(it)
        }
        gitHubUserEmail?.let {
            gitHelper.configUserEmail(it)
        }
    }

    private fun prepareGitBranch(headBranch: String): Boolean {
        gitHelper.checkout(baseBranch!!)
        gitHelper.pull()

        // Local headBranch cleanup
        commandExecutor.execute("git branch -D $headBranch", project.rootProject.projectDir, logStandardOutput = true, ignoreExitValue = true)
        gitHelper.prune()
        val execResult = commandExecutor.execute("git checkout $headBranch", project.rootProject.projectDir, logStandardOutput = true, ignoreExitValue = true)
        return if (!execResult.isSuccessful()) {
            gitHelper.createBranch(headBranch)
            true
        } else {
            // Try to merge from baseBranch to headBranch
            // TODO If there is a conflict, it will fail. Add an error message here telling that the dev need to merge and resolve the conflicts
            gitHelper.merge(baseBranch!!)
            false
        }
    }

    private fun upgradeDependencies(dependenciesLinesMap: Map<String, List<String>>, artifactsToUpgradeByGroup: List<ArtifactUpgrade>): List<UpgradeResult> {
        val dependenciesLinesMapByGroup = dependenciesLinesMap.toMutableMap()
        val upgradeResults = mutableListOf<UpgradeResult>()
        artifactsToUpgradeByGroup.forEach { artifactToUpgrade ->
            var upgradedUpgradeResult: UpgradeResult? = null

            if (artifactToUpgrade.id == ArtifactUpgrade.GRADLE_ID) {
                val gradleWrapperFile = DependenciesParser.getGradleWrapperFile(project)
                if (gradleWrapperFile.exists()) {
                    val lines = gradleWrapperFile.readLines()
                    gradleWrapperFile.bufferedWriter().use { out ->
                        lines.forEach { line ->
                            val upgradeResult = DependenciesParser.upgradeGradle(line, artifactToUpgrade)
                            if (upgradeResult.upgraded) {
                                upgradeResults.add(upgradeResult)
                                log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                                upgradedUpgradeResult = upgradeResult
                            }
                            out.write(upgradeResult.line)
                            out.newLine()
                        }
                    }
                }
            } else {
                dependenciesLinesMapByGroup.entries.forEach { entry ->
                    val newLines = mutableListOf<String>()
                    File(entry.key).bufferedWriter().use { out ->
                        entry.value.forEach { line ->
                            val upgradeResult = DependenciesParser.upgradeDependency(line, artifactToUpgrade)
                            if (upgradeResult.upgraded) {
                                upgradeResults.add(upgradeResult)
                                log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                                upgradedUpgradeResult = upgradeResult
                            }
                            newLines.add(upgradeResult.line)
                            out.write(upgradeResult.line + "\n")
                        }
                    }
                    dependenciesLinesMapByGroup[entry.key] = newLines
                }
            }

            if (pullRequestEnabled && upgradedUpgradeResult != null) {
                commit(upgradedUpgradeResult!!)
            }
            log("")
        }
        return upgradeResults
    }

    private fun commit(upgradeResult: UpgradeResult) {
        gitHelper.addAll()
        gitHelper.commit("Upgraded ${upgradeResult.artifactUpgrade} from ${upgradeResult.artifactUpgrade!!.fromVersion} to ${upgradeResult.artifactUpgrade.toVersion}")
    }

    private fun createPullRequest(upgradeResults: List<UpgradeResult>, headBranch: String, groupId: String?, group: String) {
        gitHelper.push(headBranch)
        log("The changes were pushed to $headBranch branch.")

        // We add this delay to automatically fix this: https://support.circleci.com/hc/en-us/articles/360034536433-Pull-requests-not-building-due-to-Only-build-pull-requests-settings
        ExecutorUtils.sleep(10, TimeUnit.SECONDS)

        val client = if (gitHubApiHostName.isNullOrEmpty()) {
            GitHubClient()
        } else {
            GitHubClient(gitHubApiHostName)
        }

        client.setSerializeNulls(false)
        client.setOAuth2Token(gitHubWriteToken)

        val repositoryIdProvider = RepositoryId.create(gitHubRepositoryOwner, gitHubRepositoryName)
        val pullRequestService = PullRequestService(client)

        try {
            var pullRequest = pullRequestService.getPullRequest(repositoryIdProvider, IssueService.STATE_OPEN, "$gitHubRepositoryOwner:$headBranch", baseBranch)
            if (pullRequest == null) {
                val pullRequestBody = PullRequestGenerator.createBody(upgradeResults)
                val title: String? = if (groupId == group) {
                    "Upgraded dependencies for groupId $groupId"
                } else {
                    "Upgraded $group from ${upgradeResults.first().artifactUpgrade!!.fromVersion} to ${upgradeResults.first().artifactUpgrade!!.toVersion}"
                }
                pullRequest = pullRequestService.createPullRequest(repositoryIdProvider, title, pullRequestBody, headBranch, baseBranch)
                log("The pull request #" + pullRequest!!.number + " was successfully created.")

                if (!pullRequestReviewers.isNullOrEmpty() || !pullRequestTeamReviewers.isNullOrEmpty()) {
                    val reviewRequestsService = ReviewRequestsService(client)
                    reviewRequestsService.createReviewRequest(repositoryIdProvider, pullRequest.number, pullRequestReviewers, pullRequestTeamReviewers)
                    log("Reviewers assigned to pull request #" + pullRequest.number)
                }

                if (!pullRequestLabels.isNullOrEmpty()) {
                    val labelsService = LabelsService(client)
                    labelsService.addLabelsToIssue(repositoryIdProvider, pullRequest.number, pullRequestLabels!!)
                    log("Labels assigned to pull request #" + pullRequest.number)
                }
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
}
