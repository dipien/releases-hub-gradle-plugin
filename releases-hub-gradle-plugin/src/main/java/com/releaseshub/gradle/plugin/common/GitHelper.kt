package com.releaseshub.gradle.plugin.common

class GitHelper(var commandExecutor: CommandExecutor) {

    fun configUserName(userName: String) {
        commandExecutor.execute("git config user.name $userName")
    }

    fun configUserEmail(userEmail: String) {
        commandExecutor.execute("git config user.email $userEmail")
    }

    fun checkout(branch: String) {
        commandExecutor.execute("git checkout $branch")
    }

    fun createBranch(branch: String) {
        commandExecutor.execute("git checkout -b $branch")
    }

    fun pull() {
        commandExecutor.execute("git pull")
    }

    fun addAll() {
        commandExecutor.execute("git add -A")
    }

    fun prune() {
        commandExecutor.execute("git fetch origin --prune")
    }

    fun merge(branch: String) {
        commandExecutor.execute("git merge $branch")
    }
}