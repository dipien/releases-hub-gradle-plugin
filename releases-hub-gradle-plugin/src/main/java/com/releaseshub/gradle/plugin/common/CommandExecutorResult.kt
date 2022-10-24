package com.releaseshub.gradle.plugin.common

class CommandExecutorResult(
    private val exitValue: Int
) {

    fun isSuccessful(): Boolean {
        return exitValue == 0
    }

    fun getExitValue(): Int {
        return exitValue
    }
}
