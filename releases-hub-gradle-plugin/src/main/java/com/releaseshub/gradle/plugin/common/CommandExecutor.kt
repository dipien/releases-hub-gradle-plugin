package com.releaseshub.gradle.plugin.common

import java.io.File

interface CommandExecutor {

    fun execute(commands: List<String>, workingDirectory: File? = null, logStandardOutput: Boolean = true, logErrorOutput: Boolean = true, ignoreExitValue: Boolean = false): CommandExecutorResult
}
