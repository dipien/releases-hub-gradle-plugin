package com.releaseshub.gradle.plugin.task

import com.jdroid.github.RepositoryId
import com.jdroid.github.client.GitHubClient
import com.jdroid.github.service.IssueService
import com.jdroid.github.service.LabelsService
import com.jdroid.github.service.PullRequestService
import com.jdroid.github.service.ReviewRequestsService
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus
import com.releaseshub.gradle.plugin.common.AbstractTask
import java.io.File
import java.io.IOException

open class UpgradeDependenciesTask : AbstractTask() {

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

            var groupsToUpgrade = artifactsToUpgrade.groupBy { it.groupId }.entries.toList()

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

                var dependenciesMap = dependenciesParserResult.dependenciesLinesMap
                if (!branchCreated) {
                    dependenciesMap = DependenciesParser.extractArtifacts(project, dependenciesBasePath!!, dependenciesClassNames!!, includes, excludes).dependenciesLinesMap
                }
                val upgradeResults = upgradeDependencies(dependenciesMap, artifactsToUpgradeByGroup)
                if (upgradeResults.isNotEmpty()) {
                    if (pullRequestEnabled) {
                        createPullRequest(upgradeResults, headBranch, groupId, group)
                    }
                } else {
                    if (pullRequestEnabled && !branchCreated) {
                        val execResult = commandExecutor.execute("git push origin HEAD:$headBranch", project.rootProject.projectDir, true, true)
                        if (execResult.isSuccessful()) {
                            log("Merge pushed to $headBranch branch.")
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
        commandExecutor.execute("git branch -D $headBranch", project.rootProject.projectDir, true, true)
        gitHelper.prune()
        val execResult = commandExecutor.execute("git checkout $headBranch", project.rootProject.projectDir, true, true)
        if (!execResult.isSuccessful()) {
            gitHelper.createBranch(headBranch)
            return true
        } else {
            // Try to merge from baseBranch to headBranch
            // TODO If there is a conflict, it will fail. Add an error message here telling that the dev need to merge and resolve the conflicts
            gitHelper.merge(baseBranch!!)
            return false
        }
    }

    private fun upgradeDependencies(dependenciesMap: MutableMap<String, List<String>>, artifactsToUpgrade: List<ArtifactUpgrade>): List<UpgradeResult> {
        val upgradeResults = mutableListOf<UpgradeResult>()
        artifactsToUpgrade.forEach { artifactToUpgrade ->
            var upgradedUpgradeResult: UpgradeResult? = null

            if (artifactToUpgrade.id == ArtifactUpgrade.GRADLE_ID) {
                File(project.rootProject.projectDir.absolutePath).walk().forEach { file ->
                    if (file.name == DependenciesParser.GRADLE_FILE_NAME) {
                        val lines = file.readLines()
                        file.bufferedWriter().use { out ->
                            lines.forEach { line ->
                                val upgradeResult = DependenciesParser.upgradeGradle(line, artifactToUpgrade)
                                if (upgradeResult.upgraded) {
                                    upgradeResults.add(upgradeResult)
                                    log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                                    upgradedUpgradeResult = upgradeResult
                                }
                                out.write(upgradeResult.line + "\n")
                            }
                        }
                    }
                }
            } else {
                dependenciesMap.entries.forEach { entry ->
                    File(entry.key).bufferedWriter().use { out ->
                        entry.value.forEach { line ->
                            val upgradeResult = DependenciesParser.upgradeDependency(line, artifactToUpgrade)
                            if (upgradeResult.upgraded) {
                                upgradeResults.add(upgradeResult)
                                log(" - ${upgradeResult.artifactUpgrade} ${upgradeResult.artifactUpgrade?.fromVersion} -> ${upgradeResult.artifactUpgrade?.toVersion}")
                                upgradedUpgradeResult = upgradeResult
                            }
                            out.write(upgradeResult.line + "\n")
                        }
                    }
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

        val client = GitHubClient()
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
