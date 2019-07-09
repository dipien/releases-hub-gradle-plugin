package com.releaseshub.gradle.plugin.task;


import com.releaseshub.gradle.plugin.common.AbstractTask;

import org.gradle.api.logging.LogLevel;


public class ListDependenciesTask extends AbstractTask {

	public ListDependenciesTask() {
		setDescription("List all dependencies");
	}

	@Override
	protected void onExecute() {
		getLogger().log(LogLevel.LIFECYCLE, "Hello world");
	}

}
