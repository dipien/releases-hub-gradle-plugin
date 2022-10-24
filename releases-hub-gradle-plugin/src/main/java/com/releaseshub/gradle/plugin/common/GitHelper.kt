package com.releaseshub.gradle.plugin.common

class GitHelper(var commandExecutor: CommandExecutor) {

    fun configUserName(userName: String) {
        commandExecutor.execute(listOf("git", "config", "user.name", userName))
    }

    fun configUserEmail(userEmail: String) {
        commandExecutor.execute(listOf("git", "config", "user.email", userEmail))
    }

    fun checkout(branch: String) {
        commandExecutor.execute(listOf("git", "checkout", branch))
    }

    fun createBranch(branch: String) {
        commandExecutor.execute(listOf("git", "checkout", "-b", branch))
    }

    fun pull() {
        commandExecutor.execute(listOf("git", "pull"))
    }

    fun addAll() {
        commandExecutor.execute(listOf("git", "add", "."))
    }

    fun stashAll() {
        commandExecutor.execute(listOf("git", "add", "."))
        commandExecutor.execute(listOf("git", "stash"))
    }

    fun diffHead() {
        commandExecutor.execute(listOf("git", "diff", "HEAD"))
    }

    fun status() {
        commandExecutor.execute(listOf("git", "status"))
    }

    fun prune() {
        commandExecutor.execute(listOf("git", "fetch", "origin", "--prune"))
    }

    fun merge(branch: String) {
        commandExecutor.execute(listOf("git", "merge", branch))
    }

    fun commit(message: String) {
        commandExecutor.execute(listOf("git", "commit", "-m", "\"$message\""))
    }

    fun push(headBranch: String) {
        commandExecutor.execute(listOf("git", "push", "origin", "HEAD:$headBranch"))
    }

    fun hardReset(branch: String) {
        commandExecutor.execute(listOf("git", "reset", "--hard", "origin/$branch"))
    }
}
