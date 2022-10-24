package com.releaseshub.gradle.plugin.common

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import java.io.File

class CommandExecutorImpl(private val logger: Logger, private val logLevel: LogLevel) : CommandExecutor {

    @Suppress("UNCHECKED_CAST")
    override fun execute(command: String, workingDirectory: File?, logStandardOutput: Boolean, logErrorOutput: Boolean, ignoreExitValue: Boolean): CommandExecutorResult {
        log("Executing command: $command")

        val processBuilder = ProcessBuilder()
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory)
        }
        val process = processBuilder.command(command.split(" ")).start()
        val exitVal = process.waitFor()

        if (logErrorOutput) {
            log(process.errorStream.bufferedReader().readText())
        }

        if (logStandardOutput) {
            log(process.inputStream.bufferedReader().readText())
        }

        if (!ignoreExitValue && exitVal > 0) {
            throw RuntimeException("Failed execution of command: $command")
        }

        return CommandExecutorResult(exitVal)
    }

    private fun log(message: String) {
        logger.log(logLevel, message)
    }
}
