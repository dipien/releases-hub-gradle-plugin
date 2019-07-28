package com.releaseshub.gradle.plugin.common;

import org.apache.tools.ant.types.Commandline;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CommandExecutor {
	
	private Project project;
	private LogLevel logLevel;
	
	public CommandExecutor(Project project, LogLevel logLevel) {
		this.project = project;
		this.logLevel = logLevel;
	}
	
	public ExtendedExecResult execute(String command, File workingDirectory, Boolean logStandardOutput, Boolean ignoreExitValue) {
		log("Executing command: " + command);
		
		ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
		
		ExecResult execResult = project.exec(new Action<ExecSpec>() {
			@Override
			public void execute(ExecSpec execSpec) {
				if (workingDirectory != null) {
					execSpec.setWorkingDir(workingDirectory);
				}
				execSpec.setCommandLine((Object[])Commandline.translateCommandline(command));
				execSpec.setIgnoreExitValue(ignoreExitValue);
				if (logStandardOutput) {
					execSpec.setStandardOutput(standardOutputStream);
				}
				execSpec.setErrorOutput(errorOutputStream);
			}
		});
		if (standardOutputStream.size() > 0) {
			log(standardOutputStream.toString());
		}
		if (errorOutputStream.size() > 0) {
			project.getLogger().error(errorOutputStream.toString());
		}
		return new ExtendedExecResult(execResult, standardOutputStream, errorOutputStream);
	}
	
	public ExtendedExecResult execute(String command, File workingDirectory) {
		return execute(command, workingDirectory, true, false);
	}
	
	public ExtendedExecResult execute(String command) {
		return execute(command, project.getRootProject().getProjectDir());
	}
	
	private void log(String message) {
		project.getLogger().log(logLevel, message);
	}
}
