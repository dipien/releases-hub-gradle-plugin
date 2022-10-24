package com.releaseshub.gradle.plugin.common

import java.io.File

class FakeCommandExecutor : CommandExecutor {
    override fun execute(
        commands: List<String>,
        workingDirectory: File?,
        logStandardOutput: Boolean,
        logErrorOutput: Boolean,
        ignoreExitValue: Boolean
    ): CommandExecutorResult {
        TODO("Not yet implemented")
    }
}
