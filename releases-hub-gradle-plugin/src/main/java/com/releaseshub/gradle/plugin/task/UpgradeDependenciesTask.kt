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
import com.releaseshub.gradle.plugin.context.BuildConfig
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

open class UpgradeDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "upgradeDependencies"
    }

    @get:Input
    var baseBranch: String? = null

    @get:Input
    var headBranchPrefix: String? = null

    @get:Input
    var pullRequestEnabled: Boolean = false

    @get:Input
    var pullRequestsMax: Int? = null

    @get:Input
    @get:Optional
    var pullRequestLabels: List<String>? = null

    @get:Input
    @get:Optional
    var pullRequestReviewers: List<String>? = null

    @get:Input
    @get:Optional
    var pullRequestTeamReviewers: List<String>? = null

    @get:Input
    @get:Optional
    var gitHubUserName: String? = null

    @get:Input
    @get:Optional
    var gitHubUserEmail: String? = null

    @get:Input
    var gitHubRepositoryOwner: String? = null

    @get:Input
    var gitHubRepositoryName: String? = null

    @get:Input
    @get:Optional
    var gitHubWriteToken: String? = null

    @get:Input
    @get:Optional
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

        val dependenciesParserResult = DependenciesExtractor.extractArtifacts(project.rootProject.projectDir, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes)

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
                    dependenciesLinesMap = DependenciesExtractor.extractArtifacts(project.rootProject.projectDir, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes).dependenciesLinesMap
                }
                val upgradeResults = upgradeDependencies(dependenciesLinesMap, artifactsToUpgradeByGroup)
                if (pullRequestEnabled) {
                    if (upgradeResults.isNotEmpty()) {
                        createPullRequest(upgradeResults, headBranch, groupId, group)
                    } else {
                        if (!branchCreated) {
                            val execResult = commandExecutor.execute("git push origin HEAD:$headBranch", ignoreExitValue = true)
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
        commandExecutor.execute("git branch -D $headBranch", ignoreExitValue = true)
        gitHelper.prune()
        val execResult = commandExecutor.execute("git checkout $headBranch", ignoreExitValue = true)
        return if (!execResult.isSuccessful()) {
            gitHelper.createBranch(headBranch)
            true
        } else {
            try {
                // Try to merge from baseBranch to headBranch
                gitHelper.merge(baseBranch!!)
            } catch (e: Exception) {
                gitHelper.hardReset(headBranch)
                logger.log(LogLevel.WARN, "Failed to merge from $baseBranch to $headBranch branch. Please manually resolve the conflicts.")
            }
            false
        }
    }

    private fun upgradeDependencies(dependenciesLinesMap: Map<String, List<String>>, artifactsToUpgradeByGroup: List<ArtifactUpgrade>): List<UpgradeResult> {
        val dependenciesLinesMapByGroup = dependenciesLinesMap.toMutableMap()
        val upgradeResults = mutableListOf<UpgradeResult>()
        artifactsToUpgradeByGroup.forEach { artifactToUpgrade ->
            var upgradedUpgradeResult: UpgradeResult? = null

            if (artifactToUpgrade.id == ArtifactUpgrade.GRADLE_ID) {
                val upgradeResult = DependenciesUpgrader.upgradeGradle(commandExecutor, project.rootProject.projectDir, artifactToUpgrade)
                if (upgradeResult.upgraded) {
                    upgradeResults.add(upgradeResult)
                    log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                    upgradedUpgradeResult = upgradeResult
                }
            } else {
                dependenciesLinesMapByGroup.entries.forEach { entry ->
                    val newLines = mutableListOf<String>()
                    File(entry.key).bufferedWriter().use { out ->
                        entry.value.forEach { line ->
                            val upgradeResult = DependenciesUpgrader.upgradeDependency(line, artifactToUpgrade)
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
        gitHelper.diffHead()
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
                val pullRequestBody = PullRequestGenerator.createBody(upgradeResults, BuildConfig.VERSION)
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
