package com.releaseshub.gradle.plugin.task

import com.dipien.github.PullRequest
import com.dipien.github.RepositoryId
import com.dipien.github.client.GitHubClient
import com.dipien.github.service.IssueService
import com.dipien.github.service.LabelsService
import com.dipien.github.service.PullRequestService
import com.dipien.github.service.ReviewRequestsService
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.artifacts.fetch.ArtifactUpgradeHelper
import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.context.BuildConfig
import com.releaseshub.gradle.plugin.dependencies.BasicDependenciesExtractor
import com.releaseshub.gradle.plugin.dependencies.BasicDependenciesUpgrader
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
    var pullRequestAssignee: String? = null

    @get:Input
    @get:Optional
    var pullRequestReviewers: List<String>? = null

    @get:Input
    @get:Optional
    var pullRequestTeamReviewers: List<String>? = null

    @get:Input
    @get:Optional
    var gitUserName: String? = null

    @get:Input
    @get:Optional
    var gitUserEmail: String? = null

    @get:Input
    @get:Optional
    var gitHubRepository: String? = null

    @get:Input
    @get:Optional
    var gitHubRepositoryOwner: String? = null

    @get:Input
    @get:Optional
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

        if (pullRequestEnabled) {

            if (gitHubRepository != null) {
                gitHubRepositoryOwner = gitHubRepository?.split("/")?.first()
                gitHubRepositoryName = gitHubRepository?.split("/")?.last()
            }

            getExtension().validateBaseBranch()
            getExtension().validateHeadBranchPrefix()
            getExtension().validateGitHubRepository()
            getExtension().validateGitHubWriteToken()
        }

        val extractor = BasicDependenciesExtractor(getAllDependenciesPaths())
        val dependenciesParserResult = extractor.extractArtifacts(project.rootProject.projectDir, includes, excludes)

        val artifactsToUpgrade = ArtifactUpgradeHelper.getArtifactsUpgrades(dependenciesParserResult.getAllArtifacts(), getRepositories(), true).filter { it.artifactUpgradeStatus == ArtifactUpgradeStatus.PENDING_UPGRADE }

        if (artifactsToUpgrade.isNotEmpty()) {

            var groupsToUpgrade = artifactsToUpgrade.groupBy { if (pullRequestEnabled) it.groupId else null }.entries.toList()

            if (pullRequestEnabled) {
                configureGit()
                val totalSize = groupsToUpgrade.size
                groupsToUpgrade = groupsToUpgrade.take(pullRequestsMax!!)
                log("Creating ${groupsToUpgrade.size} of $totalSize possible pull requests. Increment the \"pullRequestsMax\" property if you want more pull requests created by task execution.")
                log("")
            }

            groupsToUpgrade.forEach { (groupId, artifactsToUpgradeByGroup) ->

                log("# Processing groupId [$groupId]")
                val group: String = groupId ?: artifactsToUpgradeByGroup.first().toString()
                val headBranch = headBranchPrefix + group.replace(".", "_", true)

                var branchCreated = true
                if (pullRequestEnabled) {
                    branchCreated = prepareGitBranch(headBranch)
                }

                // Case 1: headBranch previously created, pull request open, no new upgrades => DO NOTHING
                // Case 2: headBranch previously created, pull request open, new upgrades => MERGE BRANCH & UPDATE PR BODY
                // Case 3: headBranch previously created, pull request doesn't exist no new upgrades => WARNING
                // Case 4: headBranch previously created, pull request doesn't exist, new upgrades => CREATE PR
                // Case 5: headBranch not previously created, pull request doesn't exist, no new upgrades => DO NOTHING
                // Case 6: headBranch not previously created, pull request doesn't exist, new upgrades => CREATE PR

                val upgradeResults = upgradeDependencies(dependenciesParserResult.dependenciesFiles, artifactsToUpgradeByGroup)
                if (pullRequestEnabled) {
                    if (upgradeResults.isNotEmpty()) {
                        gitHelper.push(headBranch)
                        log("The changes were pushed to $headBranch branch.")
                        createPullRequest(upgradeResults, headBranch, groupId, group)
                    } else {

                        if (branchCreated) {
                            log("* Case 5: headBranch not previously created, pull request doesn't exist, no new upgrades => DO NOTHING")
                        } else {
                            val execResult = commandExecutor.execute(listOf("git", "push", "origin", "HEAD:$headBranch"), ignoreExitValue = true)
                            if (execResult.isSuccessful()) {
                                log("Merge pushed to $headBranch branch.")
                            }

                            val pullRequest = getPullRequest(headBranch)
                            if (pullRequest != null) {
                                log("* Case 1: headBranch previously created, pull request open, no new upgrades => DO NOTHING")
                            } else {
                                project.logger.warn("Case 3: headBranch previously created, pull request doesn't exist, no new upgrades => WARNING")
                            }
                        }
                    }
                }

                log("##########################")
            }
        } else {
            log("No dependencies upgraded")
        }
    }

    private fun configureGit() {
        if (!gitUserName.isNullOrBlank()) {
            gitHelper.configUserName(gitUserName!!)
        }
        if (!gitUserEmail.isNullOrBlank()) {
            gitHelper.configUserEmail(gitUserEmail!!)
        }
    }

    private fun prepareGitBranch(headBranch: String): Boolean {
        gitHelper.stashAll()
        gitHelper.checkout(baseBranch!!)
        gitHelper.pull()

        // Local headBranch cleanup
        commandExecutor.execute(listOf("git", "branch", "-D", headBranch), ignoreExitValue = true)
        gitHelper.prune()
        val execResult = commandExecutor.execute(listOf("git", "checkout", headBranch), ignoreExitValue = true)
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

    private fun upgradeDependencies(dependenciesFiles: List<File>, artifactsToUpgradeByGroup: List<ArtifactUpgrade>): List<UpgradeResult> {
        val upgradeResults = mutableListOf<UpgradeResult>()
        val upgrader = BasicDependenciesUpgrader(commandExecutor)
        artifactsToUpgradeByGroup.forEach { artifactToUpgrade ->
            var upgradedUpgradeResult: UpgradeResult? = null
            dependenciesFiles.forEach { dependenciesFile ->
                val upgradeResult = upgrader.upgradeDependenciesFile(project.rootDir, dependenciesFile, artifactToUpgrade)
                if (upgradeResult != null && upgradeResult.upgraded) {
                    upgradedUpgradeResult = upgradeResult
                    upgradeResults.add(upgradeResult)
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
        try {
            gitHelper.commit("Upgraded ${upgradeResult.artifactUpgrade} from ${upgradeResult.artifactUpgrade!!.fromVersion} to ${upgradeResult.artifactUpgrade.toVersion}")
        } catch (e: Exception) {
            // FIXME This is logging to detect the cause of a crash
            println("upgradeResult: $upgradeResult")
            println("upgradeResult.artifactUpgrade: ${upgradeResult.artifactUpgrade}")
            println("upgradeResult.artifactUpgrade!!.fromVersion: ${upgradeResult.artifactUpgrade!!.fromVersion}")
            println("upgradeResult.artifactUpgrade!!.fromVersion: ${upgradeResult.artifactUpgrade.toVersion}")
            throw e
        }
    }

    private fun getPullRequest(headBranch: String): PullRequest? {
        val client = if (gitHubApiHostName.isNullOrEmpty()) {
            GitHubClient()
        } else {
            GitHubClient(gitHubApiHostName)
        }

        client.setSerializeNulls(false)
        client.setOAuth2Token(gitHubWriteToken)

        val repositoryIdProvider = RepositoryId.create(gitHubRepositoryOwner, gitHubRepositoryName)
        val pullRequestService = PullRequestService(client)
        return pullRequestService.getPullRequest(repositoryIdProvider, IssueService.STATE_OPEN, "$gitHubRepositoryOwner:$headBranch", baseBranch)
    }

    private fun createPullRequest(upgradeResults: List<UpgradeResult>, headBranch: String, groupId: String?, group: String) {

        // We add this delay to automatically fix this: https://support.circleci.com/hc/en-us/articles/360034536433-Pull-requests-not-building-due-to-Only-build-pull-requests-settings
        Thread.sleep(TimeUnit.SECONDS.toMillis(10))

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

                log("Case 4 or 6: pull request doesn't exist, new upgrades => CREATE PR")

                log("Pull request with head [$gitHubRepositoryOwner:$headBranch] and base [$baseBranch] not found")
                val pullRequestBody = PullRequestGenerator.createBody(upgradeResults, BuildConfig.VERSION)
                val title: String = if (groupId == group) {
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

                if (!pullRequestAssignee.isNullOrEmpty()) {
                    val issueService = IssueService(client)
                    issueService.addAssignee(repositoryIdProvider, pullRequest.number, pullRequestAssignee)
                    log("""User [$pullRequestAssignee] assigned to pull request #${pullRequest.number}""")
                }
            } else {
                log("Case 2: headBranch previously created, pull request open, new upgrades => MERGE BRANCH & ADD COMMENT TO PR")
                commentPullRequest(pullRequest, PullRequestGenerator.createComment(upgradeResults))
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun commentPullRequest(pullRequest: PullRequest, pullRequestComment: String) {
        val client = if (gitHubApiHostName.isNullOrEmpty()) {
            GitHubClient()
        } else {
            GitHubClient(gitHubApiHostName)
        }

        client.setSerializeNulls(false)
        client.setOAuth2Token(gitHubWriteToken)

        val repositoryIdProvider = RepositoryId.create(gitHubRepositoryOwner, gitHubRepositoryName)
        val issueService = IssueService(client)
        issueService.createComment(repositoryIdProvider, pullRequest.number, pullRequestComment)
        log("Added comment to pull request #" + pullRequest.number)
    }
}
