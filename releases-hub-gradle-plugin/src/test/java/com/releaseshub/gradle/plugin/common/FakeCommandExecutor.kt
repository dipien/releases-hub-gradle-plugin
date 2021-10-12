package com.releaseshub.gradle.plugin.common

import java.io.File

class FakeCommandExecutor : CommandExecutor {
    override fun execute(
        command: String,
        workingDirectory: File?,
        logStandardOutput: Boolean,
        logErrorOutput: Boolean,
        ignoreExitValue: Boolean
    ): ExtendedExecResult {
        TODO("Not yet implemented")
    }
}
