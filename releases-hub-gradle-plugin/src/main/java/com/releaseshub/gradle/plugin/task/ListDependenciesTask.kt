package com.releaseshub.gradle.plugin.task


import com.releaseshub.gradle.plugin.common.AbstractTask

import org.gradle.api.logging.LogLevel


open class ListDependenciesTask : AbstractTask() {

	init {
		description = "List all dependencies"
	}

	override fun onExecute() {
		logger.log(LogLevel.LIFECYCLE, "Hello world with kotlin")
	}

}
