package com.releaseshub.gradle.plugin.common

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

object LoggerHelper {

    lateinit var logger: Logger
    lateinit var logLevel: LogLevel

    fun log(message: String) {
        logger.log(logLevel, message)
    }

}