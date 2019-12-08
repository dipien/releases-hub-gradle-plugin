package com.releaseshub.gradle.plugin.common

import java.io.ByteArrayOutputStream
import java.io.File
import org.apache.tools.ant.types.Commandline
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.process.internal.ExecException

class CommandExecutor(private val project: Project, private val logLevel: LogLevel) {

    @Suppress("UNCHECKED_CAST")
    fun execute(command: String, workingDirectory: File? = project.rootProject.projectDir, logStandardOutput: Boolean = true, ignoreExitValue: Boolean = false): ExtendedExecResult {
        log("Executing command: $command")

        val standardOutputStream = ByteArrayOutputStream()
        val errorOutputStream = ByteArrayOutputStream()

        try {
            val execResult = project.exec { execSpec ->
                if (workingDirectory != null) {
                    execSpec.workingDir = workingDirectory
                }
                execSpec.setCommandLine(*Commandline.translateCommandline(command) as Array<Any>)
                execSpec.isIgnoreExitValue = ignoreExitValue
                if (logStandardOutput) {
                    execSpec.standardOutput = standardOutputStream
                }
                execSpec.errorOutput = errorOutputStream
            }
            if (standardOutputStream.size() > 0) {
                log(standardOutputStream.toString())
            }

            if (errorOutputStream.size() > 0) {
                project.logger.error(errorOutputStream.toString())
            }
            return ExtendedExecResult(execResult, standardOutputStream, errorOutputStream)
        } catch (e: ExecException) {
            if (errorOutputStream.size() > 0) {
                project.logger.error(errorOutputStream.toString())
            }
            throw e
        }
    }

    private fun log(message: String) {
        project.logger.log(logLevel, message)
    }
}
